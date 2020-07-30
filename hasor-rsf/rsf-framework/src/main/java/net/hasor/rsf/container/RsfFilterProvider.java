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
import net.hasor.core.AppContext;
import net.hasor.rsf.RsfFilter;

import java.util.function.Supplier;

/**
 * RsfFilter的{@link Supplier}封装形式。
 * @version : 2014年7月8日
 * @author 赵永春 (zyc@hasor.net)
 */
public class RsfFilterProvider implements Supplier<RsfFilter> {
    private Class<? extends RsfFilter> rsfFilterType;
    private AppContext                 appContext;

    public RsfFilterProvider(AppContext appContext, Class<? extends RsfFilter> rsfFilterType) {
        this.appContext = appContext;
        this.rsfFilterType = rsfFilterType;
    }

    public RsfFilter get() {
        return this.appContext.getInstance(this.rsfFilterType);
    }
}