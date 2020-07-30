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
import net.hasor.utils.StringUtils;
import net.hasor.web.Invoker;
import net.hasor.web.wrap.InvokerWrap;

import java.util.*;

/**
 * 表单验证器，Invoker 扩展实现类。
 * @version : 2017-01-10
 * @author 赵永春 (zyc@hasor.net)
 */
public class ValidInvokerSupplier extends InvokerWrap implements ValidInvoker {
    private final Map<String, ValidItem> validData = new LinkedHashMap<>();

    protected ValidInvokerSupplier(Invoker context) {
        super(context);
    }

    protected Map<String, ValidItem> getValidData() {
        return this.validData;
    }

    @Override
    public List<String> validKeys() {
        return new ArrayList<>(this.validData.keySet());
    }

    @Override
    public List<Message> validErrorsOfMessage(String key) {
        ValidItem data = this.validData.get(key);
        return data == null ? Collections.EMPTY_LIST : new ArrayList<>(data);
    }

    @Override
    public Message firstValidErrorsOfMessage(String key) {
        ValidItem data = this.validData.get(key);
        return data == null ? null : data.firstError();
    }

    @Override
    public boolean isValid() {
        for (ValidItem data : this.validData.values()) {
            if (data != null && !data.isValid()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isValid(String key) {
        ValidItem data = this.validData.get(key);
        return data == null || data.isValid();
    }

    @Override
    public void clearValidErrors(String key) {
        this.validData.remove(key);
    }

    @Override
    public void addErrors(String key, List<Message> validMessage) {
        if (StringUtils.isBlank(key)) {
            throw new NullPointerException("valid error message key is null.");
        }
        ValidItem newDate = new ValidItem(key);
        newDate.addAll(validMessage);
        errors(newDate);
    }

    protected void errors(ValidItem validItem) {
        if (validItem == null) {
            return;
        }
        ValidItem messages = this.validData.get(validItem.getKey());
        if (messages == null) {
            this.validData.put(validItem.getKey(), validItem);
        } else {
            messages.addAll(validItem);
        }
    }
}