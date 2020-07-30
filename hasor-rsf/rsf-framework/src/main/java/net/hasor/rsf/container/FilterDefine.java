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
package net.hasor.rsf.container;
import net.hasor.rsf.RsfFilter;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * 获取服务上配置有效的过滤器。
 * @version : 2014年11月12日
 * @author 赵永春 (zyc@hasor.net)
 */
class FilterDefine implements Supplier<RsfFilter> {
    private String                        filterID;
    private Supplier<? extends RsfFilter> filterProvider;

    //
    public FilterDefine(String filterID, Supplier<? extends RsfFilter> provider) {
        this.filterID = filterID;
        this.filterProvider = Objects.requireNonNull(provider);
    }
    //

    /**过滤器ID*/
    public String filterID() {
        return this.filterID;
    }

    @Override
    public RsfFilter get() {
        return filterProvider.get();
    }

    public Supplier<? extends RsfFilter> getProvider() {
        return filterProvider;
    }

    public String toString() {
        return "[" + filterID + "]";
    }
}