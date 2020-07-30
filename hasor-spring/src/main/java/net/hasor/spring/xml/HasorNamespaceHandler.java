/*
 * Copyright 2002-2005 the original author or authors.
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
package net.hasor.spring.xml;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * 负责注册 h:hasor、h:bean 标签解析器
 * @version : 2016年2月16日
 * @author 赵永春 (zyc@hasor.net)
 */
public class HasorNamespaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {
        registerBeanDefinitionParser("hasor", new HasorDefinitionParser());
        registerBeanDefinitionParser("bean", new BeanDefinitionParser());
    }
}
