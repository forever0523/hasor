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
package net.hasor.core;
/**
 * Intercepts calls on an interface on its way to the target. These are nested "on top" of the target.
 * <p>
 * The user should implement the {@link #invoke(MethodInvocation)} method to
 * modify the original behavior. E.g. the following class implements a tracing
 * interceptor (traces all the calls on the intercepted method(s)):
 *
 * <pre class=code>
 * class TracingInterceptor implements MethodInterceptor {
 * 	Object invoke(MethodInvocation i) throws Throwable {
 * 		System.out.println(&quot;method &quot; + i.getMethod() + &quot; is called on &quot;
 * 				+ i.getThis() + &quot; with args &quot; + i.getArguments());
 * 		Object ret = i.proceed();
 * 		System.out.println(&quot;method &quot; + i.getMethod() + &quot; returns &quot; + ret);
 * 		return ret;
 *    }
 * }
 * </pre>
 */
@FunctionalInterface
public interface MethodInterceptor {
    /**
     * implement this method to perform extra treatments before and after the invocation.
     * @param invocation 拦截器链
     * @return 返回拦截器执行结果
     * @throws Throwable 抛出的异常
     */
    public Object invoke(MethodInvocation invocation) throws Throwable;
}