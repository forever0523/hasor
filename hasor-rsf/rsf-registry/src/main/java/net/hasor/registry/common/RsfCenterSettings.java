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
package net.hasor.registry.common;
import net.hasor.core.Settings;
import net.hasor.registry.common.CenterMode;
import net.hasor.rsf.InterAddress;

/**
 *
 * @version : 2014年11月12日
 * @author 赵永春 (zyc@hasor.net)
 */
public interface RsfCenterSettings extends Settings {
    /** 工作模式 */
    public CenterMode getMode();

    /**
     * center地址列表
     * <li>none模式下，配置无效。<li/>
     * <li>server模式下，配置无效。<li/>
     * <li>cleint模式下，用于连接到这些center地址上。<li/>
     * <li>cluster模式下，用于center集群互相同步数据。<li/>
     */
    public InterAddress[] getCenterServerSet();

    /** 与RSF center 通信的RPC接口调用超时时间 */
    public int getCenterRsfTimeout();

    /** 服务心跳时间 */
    public int getHeartbeatTime();

    /**获取app key ID*/
    public String getAppKeyID();

    /**获取app key 秘钥*/
    public String getAppKeySecret();
}