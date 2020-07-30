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
package net.hasor.registry.server.manager;
import net.hasor.core.ImplBy;
import net.hasor.registry.client.domain.ServiceID;
import net.hasor.registry.server.domain.AuthBean;
import net.hasor.registry.server.domain.Result;
import net.hasor.rsf.domain.RsfServiceType;

/**
 * 接口授权查询。
 * @version : 2016年2月22日
 * @author 赵永春 (zyc@hasor.net)
 */
@ImplBy(FileAuthQuery.class)
public interface AuthQuery {
    /**
     *
     * @param authInfo
     */
    public Result<Boolean> checkKeySecret(AuthBean authInfo);

    /**
     * 发布服务权限检测
     * @param authInfo 授权信息
     * @param serviceInfo 服务信息
     * @param serviceType 提供者 or 消费者
     */
    public Result<Boolean> checkPublish(AuthBean authInfo, ServiceID serviceInfo, RsfServiceType serviceType);
}