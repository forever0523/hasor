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
package net.hasor.registry.server;
import net.hasor.core.AppContext;
import net.hasor.core.LifeModule;
import net.hasor.registry.client.RsfCenterListener;
import net.hasor.registry.client.RsfCenterRegister;
import net.hasor.registry.common.RsfCenterSettings;
import net.hasor.registry.server.manager.AuthQuery;
import net.hasor.registry.storage.DataAdapter;
import net.hasor.rsf.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 注册中心启动入口。
 *
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2015年5月5日
 */
public class RegistryServerModule implements RsfModule, LifeModule {
    protected Logger         logger         = LoggerFactory.getLogger(getClass());
    protected ServerSettings serverSettings = null;

    public RegistryServerModule(RsfEnvironment rsfEnvironment, RsfCenterSettings settings) throws ClassNotFoundException {
        this.serverSettings = new ServerSettings(rsfEnvironment, settings);
    }

    //
    @Override
    public void loadModule(RsfApiBinder apiBinder) throws Throwable {
        apiBinder.bindType(ServerSettings.class).toInstance(this.serverSettings);
    }

    public final void onStart(AppContext appContext) throws Throwable {
        RsfContext rsfContext = appContext.getInstance(RsfContext.class);
        rsfContext.getAppContext().getInstance(AuthQuery.class);
        rsfContext.getAppContext().getInstance(DataAdapter.class);
        //
        rsfContext.offline();   //切换下线，暂不接收任何Rsf请求
        doStartCenter(rsfContext);
        rsfContext.online();    //切换上线，开始提供服务
        this.logger.info("rsfCenter online.");
    }

    /** Center启动 */
    protected void doStartCenter(RsfContext rsfContext) throws IOException {
        //
        RsfPublisher rsfBinder = rsfContext.publisher();
        rsfBinder.rsfService(RsfCenterRegister.class).to(RsfCenterRegisterProvider.class)//
                .bindFilter("VerificationFilter", RsfCenterServerVerifyFilter.class)//
                .register();
        rsfBinder.rsfService(RsfCenterListener.class)// 
                .bindFilter("VerificationFilter", RsfCenterServerVerifyFilter.class)//
                .register();
    }

    /** Center停止 */
    public void onStop(AppContext appContext) throws Throwable {
        /**/
    }
}