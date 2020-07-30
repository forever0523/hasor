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
import net.hasor.utils.ExceptionUtils;

/**
 *  Provider 接口的变版本，提供了一个新方法 getWithThrow。通过这个方法允许 get 抛出异常。
 * @version : 2014年5月22日
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ProviderWithThrow<T> extends Provider<T> {
    /** @return 获取对象。*/
    public default T get() {
        try {
            return getWithThrow();
        } catch (Throwable e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }

    /** @return 获取对象。*/
    public T getWithThrow() throws Throwable;
}