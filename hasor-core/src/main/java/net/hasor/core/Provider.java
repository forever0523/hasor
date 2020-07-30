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
import java.util.function.Supplier;

/**
 *  提供者实现这个接口就相当于同时实现了，java.util.function.Supplier、javax.inject.Provider 两个接口
 * @version : 2014年5月22日
 * @author 赵永春 (zyc@byshell.org)
 */
public interface Provider<T> extends Supplier<T>, javax.inject.Provider<T> {
    /** @return 获取对象。*/
    public T get();

    public default Supplier<T> toSupplier(Provider<T> provider) {
        return provider;
    }

    public default Supplier<T> toSupplier(javax.inject.Provider<T> provider) {
        return provider::get;
    }

    public default Provider<T> fromSupplier(Supplier<T> supplier) {
        return supplier::get;
    }

    public default Provider<T> fromProvider(javax.inject.Provider<T> supplier) {
        return supplier::get;
    }
}