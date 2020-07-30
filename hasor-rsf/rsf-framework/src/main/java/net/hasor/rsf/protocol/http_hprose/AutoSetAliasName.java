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
package net.hasor.rsf.protocol.http_hprose;
import net.hasor.core.EventContext;
import net.hasor.core.EventListener;
import net.hasor.rsf.*;
import net.hasor.rsf.container.RsfDomainProvider;
import net.hasor.rsf.domain.RsfEvent;
import net.hasor.rsf.domain.ServiceDomain;
import net.hasor.utils.StringUtils;

import static io.netty.handler.codec.http.HttpHeaders.Names.LOCATION;
import static io.netty.handler.codec.http.HttpHeaders.Names.ORIGIN;

/**
 * Hprose 服务自动设定别名工具
 * @version : 2017年02月23日
 * @author 赵永春 (zyc@hasor.net)
 */
public class AutoSetAliasName implements RsfModule, EventListener<Object> {
    @Override
    public void loadModule(RsfApiBinder apiBinder) throws Throwable {
        // .添加 Listener 用于监听 Provider、Consumer 的注册事件，在事件处理中补增 Hprose 别名。
        RsfEnvironment env = apiBinder.getEnvironment();
        EventContext eventContext = env.getEventContext();
        eventContext.addListener(RsfEvent.Rsf_ProviderService, this);
        eventContext.addListener(RsfEvent.Rsf_ConsumerService, this);
        //
        // .增加 RsfFilter，在请求头中新增一个 Option，用于增加 Http 跨域调用的 Header。
        apiBinder.bindFilter(HproseConstants.HPROSE + "_AliasNameFilter", new RsfFilter() {
            public void doFilter(RsfRequest request, RsfResponse response, RsfFilterChain chain) throws Throwable {
                response.addOption(LOCATION, request.getOption(LOCATION));
                response.addOption(ORIGIN, request.getOption(ORIGIN));
                chain.doFilter(request, response);
            }
        });
    }

    @Override
    public void onEvent(String event, Object eventData) throws Throwable {
        if (!(eventData instanceof RsfDomainProvider)) {
            return;
        }
        //
        RsfDomainProvider domainProvider = (RsfDomainProvider) eventData;
        ServiceDomain domain = domainProvider.getDomain();
        String simpleName = domain.getBindType().getSimpleName();
        simpleName = StringUtils.firstCharToLowerCase(simpleName);
        //
        domain.putAliasName(HproseConstants.HPROSE, simpleName);
    }
}