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
import net.hasor.core.spi.AppContextAware;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfContext;

import java.util.function.Supplier;

/**
 * RsfFilter的{@link Supplier}封装形式。
 * @version : 2014年7月8日
 * @author 赵永春 (zyc@hasor.net)
 */
class InnerRsfObjectProvider<T> implements Supplier<T>, AppContextAware {
    private RsfBindInfo<T> bindInfo;
    private AppContext     appContext;

    public InnerRsfObjectProvider(RsfBindInfo<T> bindInfo) {
        this.bindInfo = bindInfo;
    }

    @Override
    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
    }

    @Override
    public T get() {
        RsfContext rsfCenter = this.appContext.getInstance(RsfContext.class);
        return rsfCenter.getRsfClient().getRemote(this.bindInfo);
    }
}