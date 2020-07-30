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
package net.hasor.web;
/**
 * 请求调用链
 * @version : 2016-12-24
 * @author 赵永春 (zyc@hasor.net)
 */
@FunctionalInterface
public interface InvokerChain {
    /**
     * 继续执行后续的请求过滤器链。
     * @param invoker 当前请求对象
     * @throws Throwable 请求过程中抛出的异常。
     */
    public Object doNext(Invoker invoker) throws Throwable;
}