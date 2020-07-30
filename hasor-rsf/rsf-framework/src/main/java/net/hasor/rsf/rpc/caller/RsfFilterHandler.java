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
package net.hasor.rsf.rpc.caller;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfFilterChain;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.RsfResponse;

import java.util.function.Supplier;

/**
 * 负责处理 RsfFilter 调用
 * @version : 2014年11月4日
 * @author 赵永春 (zyc@hasor.net)
 */
public class RsfFilterHandler implements RsfFilterChain {
    private static final Supplier[]            EMPTY_FILTER = new Supplier[0];
    private final        Supplier<RsfFilter>[] rsfFilter;
    private final        RsfFilterChain        rsfChain;
    private              int                   index;

    public RsfFilterHandler(final Supplier<RsfFilter>[] rsfFilter, final RsfFilterChain rsfChain) {
        this.rsfChain = rsfChain;
        this.index = -1;
        this.rsfFilter = (rsfFilter != null && rsfFilter.length != 0) ? rsfFilter : EMPTY_FILTER;
    }

    public void doFilter(RsfRequest request, RsfResponse response) throws Throwable {
        this.index++;
        if (this.index < this.rsfFilter.length) {
            this.rsfFilter[index].get().doFilter(request, response, this);
        } else {
            this.rsfChain.doFilter(request, response);
        }
    }
}