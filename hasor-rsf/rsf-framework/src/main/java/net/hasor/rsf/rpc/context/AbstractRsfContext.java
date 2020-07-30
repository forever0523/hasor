/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.rsf.rpc.context;
import net.hasor.core.AppContext;
import net.hasor.core.EventContext;
import net.hasor.core.spi.ContextShutdownListener;
import net.hasor.core.spi.ContextStartListener;
import net.hasor.rsf.*;
import net.hasor.rsf.address.DiskCacheAddressPool;
import net.hasor.rsf.container.RsfBeanContainer;
import net.hasor.rsf.domain.*;
import net.hasor.rsf.domain.provider.AddressProvider;
import net.hasor.rsf.domain.provider.InstanceAddressProvider;
import net.hasor.rsf.domain.provider.PoolAddressProvider;
import net.hasor.rsf.rpc.caller.SenderListener;
import net.hasor.rsf.rpc.caller.remote.RemoteRsfCaller;
import net.hasor.rsf.rpc.client.RpcRsfClient;
import net.hasor.rsf.rpc.net.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * 服务上下文，负责提供 RSF 运行环境的支持。
 *
 * @version : 2014年11月12日
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractRsfContext implements RsfContext, ContextStartListener, ContextShutdownListener {
    protected     Logger               logger = LoggerFactory.getLogger(getClass());
    private final RsfBeanContainer     rsfBeanContainer; // 服务管理
    private final RsfEnvironment       rsfEnvironment;   // 环境&配置
    private final RemoteRsfCaller      rsfCaller;        // 调用器
    private final RsfNetManager        rsfNetManager;    // 网络管理器
    private final DiskCacheAddressPool addressPool;      // 地址管理器
    private final PoolAddressProvider  poolProvider;     // 地址获取接口（addressPool的另一个形态）
    private final AtomicBoolean        onlineStatus;     // 在线状态
    private       AppContext           appContext;       // 上下文环境

    public AbstractRsfContext(RsfEnvironment rsfEnvironment) {
        this.addressPool = new DiskCacheAddressPool(rsfEnvironment);
        this.poolProvider = new PoolAddressProvider(this.addressPool);
        this.rsfBeanContainer = new RsfBeanContainer(this.addressPool);
        this.rsfEnvironment = rsfEnvironment;
        Transport transport = new Transport();
        this.rsfNetManager = new RsfNetManager(rsfEnvironment, transport);
        this.rsfCaller = new RemoteRsfCaller(this, this.rsfBeanContainer, transport);
        this.onlineStatus = new AtomicBoolean(false);
    }

    @Override
    public synchronized void doStart(AppContext appContext) {
        boolean enable = this.rsfEnvironment.getSettings().getBoolean("hasor.rsfConfig.enable", false);
        if (!enable) {
            logger.info("rsf framework disable -> 'hasor.rsfConfig.enable' is false");
            return;
        }
        //
        // .枚举 loadModule 期间注册的 Service
        this.appContext = appContext;
        this.logger.info("rsfContext -> doStart , lookUp services for loadModule phase.");
        this.rsfBeanContainer.lookUp(appContext);
        //
        // .启动网络通信（默认为：offline 状态）
        this.rsfNetManager.start(appContext);
        //
        Set<String> protocols = this.rsfNetManager.runProtocols();
        if (protocols == null || protocols.isEmpty()) {
            throw new IllegalStateException("not running any protocol, please check the configuration.");
        }
        for (String protocol : protocols) {
            InterAddress interAddress = this.bindAddress(protocol);
            this.logger.info("rsfContext -> doStart , bindAddress : {}", interAddress.toHostSchema());
        }
    }

    @Override
    public void doStartCompleted(AppContext appContext) {
        //
        // .启动地址本恢复（after LifeModule.doStart）
        this.addressPool.restoreConfig();/*恢复地址本*/
        this.addressPool.startTimer();
        // .处理是否要自动上线
        if (this.rsfEnvironment.getSettings().isAutomaticOnline()) {
            this.online();
        }
        this.logger.info("rsf framework started.");
    }

    @Override
    public void doShutdown(AppContext appContext) {
        this.logger.info("rsf framework shutdown.");
        //
        this.offline();
        this.rsfCaller.shutdown();
        this.rsfNetManager.shutdown();
        this.addressPool.shutdownTimer();
    }

    @Override
    public void doShutdownCompleted(AppContext appContext) {
    }

    /**应用上线(先置为上线，在引发事件)*/
    @Override
    public synchronized void online() {
        if (!this.onlineStatus.compareAndSet(false, true)) {
            this.logger.error("rsfContext -> already online");
            return;
        }
        this.logger.info("rsfContext -> already online , fireSyncEvent ,eventType = {}", RsfEvent.Rsf_Online);
        EventContext ec = getAppContext().getEnvironment().getEventContext();
        try {
            ec.fireSyncEvent(RsfEvent.Rsf_Online, this);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**应用下线(先置为下线，在引发事件)*/
    @Override
    public synchronized void offline() {
        if (!this.onlineStatus.compareAndSet(true, false)) {
            this.logger.error("rsfContext -> already offline");
            return;
        }
        this.logger.info("rsfContext -> already offline , fireSyncEvent ,eventType = {}", RsfEvent.Rsf_Online);
        EventContext ec = getAppContext().getEnvironment().getEventContext();
        try {
            ec.fireSyncEvent(RsfEvent.Rsf_Offline, this);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public boolean isOnline() {
        return this.onlineStatus.get();
    }

    @Override
    public String getInstanceID() {
        return this.rsfEnvironment.getInstanceID();
    }

    @Override
    public AppContext getAppContext() {
        return this.appContext;
    }

    /**获取运行着哪些协议*/
    @Override
    public Set<String> runProtocols() {
        return this.rsfNetManager.runProtocols();
    }

    @Override
    public String getDefaultProtocol() {
        return this.rsfEnvironment.getSettings().getDefaultProtocol();
    }

    @Override
    public RsfEnvironment getEnvironment() {
        return this.rsfEnvironment;
    }

    public RsfSettings getSettings() {
        return this.rsfEnvironment.getSettings();
    }

    public RsfUpdater getUpdater() {
        return this.addressPool;
    }

    public ClassLoader getClassLoader() {
        return this.rsfEnvironment.getClassLoader();
    }

    @Override
    public InterAddress bindAddress(String protocol) {
        Connector connector = this.rsfNetManager.findConnector(protocol);
        if (connector == null)
            return null;
        return connector.getBindAddress();
    }

    public RsfClient getRsfClient() {
        return new RpcRsfClient(this.poolProvider, this.rsfCaller);
    }

    public RsfClient getRsfClient(String targetStr) throws URISyntaxException, UnknownHostException {
        return this.getRsfClient(new InterAddress(targetStr));
    }

    public RsfClient getRsfClient(URI targetURL) throws UnknownHostException {
        return this.getRsfClient(new InterAddress(targetURL));
    }

    public RsfClient getRsfClient(InterAddress target) {
        AddressProvider provider = new InstanceAddressProvider(target);
        return new RpcRsfClient(provider, this.rsfCaller);
    }

    public <T> RsfBindInfo<T> getServiceInfo(String serviceID) {
        return (RsfBindInfo<T>) this.rsfBeanContainer.getRsfBindInfo(serviceID);
    }

    public <T> RsfBindInfo<T> getServiceInfo(String aliasType, String aliasName) {
        return (RsfBindInfo<T>) this.rsfBeanContainer.getRsfBindInfo(aliasType, aliasName);
    }

    public <T> RsfBindInfo<T> getServiceInfo(Class<T> serviceType) {
        return this.rsfBeanContainer.getRsfBindInfo(serviceType);
    }

    public <T> RsfBindInfo<T> getServiceInfo(String group, String name, String version) {
        return (RsfBindInfo<T>) this.rsfBeanContainer.getRsfBindInfo(group, name, version);
    }

    public List<String> getServiceIDs() {
        return this.rsfBeanContainer.getServiceIDs();
    }

    public List<String> getServiceIDs(String aliasType) {
        return this.rsfBeanContainer.getServiceIDs(aliasType);
    }

    public <T> Supplier<T> getServiceProvider(RsfBindInfo<T> bindInfo) {
        return this.rsfBeanContainer.getProvider(bindInfo);
    }

    public RsfPublisher publisher() {
        return this.rsfBeanContainer.createPublisher(this.rsfBeanContainer, this);
    }

    /*接收到网络数据 & 发送网络数据*/
    private class Transport extends ReceivedAdapter implements SenderListener {
        @Override
        public void receivedMessage(InterAddress form, ResponseInfo response) {
            rsfCaller.putResponse(response);
        }

        @Override
        public void receivedMessage(InterAddress form, RequestInfo request) {
            rsfCaller.onRequest(form, request);
        }

        @Override
        public void sendRequest(InterAddress toAddress, RequestInfo info, SendCallBack callBack) {
            try {
                Connector connector = findConnector(toAddress);
                RsfChannel channel = connector.getOrConnectionTo(toAddress).get();
                if (channel != null) {
                    channel.sendData(info, callBack);
                } else {
                    throw new RsfException(ProtocolStatus.NetworkError, "Invalid address ->" + toAddress.toHostSchema());
                }
            } catch (Throwable e) {
                addressPool.invalidAddress(toAddress);//异常地址失效
                rsfCaller.putResponse(info.getRequestID(), e);
                logger.error("sendRequest - " + e.getMessage());
            }
        }

        @Override
        public void sendResponse(InterAddress toAddress, ResponseInfo info, SendCallBack callBack) {
            try {
                Connector connector = findConnector(toAddress);
                RsfChannel channel = connector.getOrConnectionTo(toAddress).get();
                if (channel != null) {
                    channel.sendData(info, callBack);
                } else {
                    throw new RsfException(ProtocolStatus.NetworkError, "Invalid address ->" + toAddress.toHostSchema());
                }
            } catch (Throwable e) {
                addressPool.invalidAddress(toAddress);//异常地址失效
                logger.error("sendResponse - " + e.getMessage(), e);
            }
        }
    }

    private Connector findConnector(InterAddress target) {
        String sechma = target.getSechma();
        Connector connector = rsfNetManager.findConnectorBySechma(sechma);// tips：例如：如果本地都不支持 rsf 协议，那么也没有必要连接远程的 rsf 协议。
        if (connector == null) {
            throw new RsfException(ProtocolStatus.ProtocolUndefined, "protocol is not support, invalid address ->" + target.toHostSchema());
        }
        return connector;
    }
}
