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
package net.hasor.web.valid;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerCreator;

/**
 * 表单验证器，Invoker扩展。
 * @version : 2017-01-10
 * @author 赵永春 (zyc@hasor.net)
 */
public class ValidInvokerCreator implements InvokerCreator {
    @Override
    public Invoker createExt(Invoker dataContext) {
        ValidInvokerSupplier supplier = new ValidInvokerSupplier(dataContext);
        supplier.put(ValidInvoker.VALID_DATA_KEY, supplier.getValidData());
        supplier.lockKey(ValidInvoker.VALID_DATA_KEY);
        return supplier;
    }
}