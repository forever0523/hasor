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
package net.hasor.web.objects;
import net.hasor.utils.StringUtils;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerChain;
import net.hasor.web.InvokerFilter;
import net.hasor.web.Mapping;
import net.hasor.web.annotation.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 启用跨域
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-20
 */
public class CorsFilter implements InvokerFilter {
    @Override
    public Object doInvoke(Invoker invoker, InvokerChain chain) throws Throwable {
        String httpMethod = invoker.getHttpRequest().getMethod();
        Mapping mapping = invoker.ownerMapping();
        if (mapping != null && mapping.findMethod(HttpMethod.OPTIONS) != null) {
            return chain.doNext(invoker);
        }
        //
        HttpServletRequest httpRequest = invoker.getHttpRequest();
        HttpServletResponse httpResponse = invoker.getHttpResponse();
        //
        String originString = httpRequest.getHeader("Origin");
        if (StringUtils.isNotBlank(originString)) {
            httpResponse.setHeader("Access-Control-Allow-Origin", originString);
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        } else {
            httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        }
        httpResponse.addHeader("Access-Control-Allow-Methods", "*");
        httpResponse.addHeader("Access-Control-Allow-Headers", "content-type");
        httpResponse.addHeader("Access-Control-Max-Age", "3600");
        //
        if (HttpMethod.OPTIONS.equalsIgnoreCase(httpMethod)) {
            return null;
        } else {
            return chain.doNext(invoker);
        }
    }
}
