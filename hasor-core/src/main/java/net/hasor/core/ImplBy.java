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
import net.hasor.core.ApiBinder.LinkedBindingBuilder;

import java.lang.annotation.*;

/**
 * <p>标记当前类型的具体实现。</p>
 *
 * 如果通过 {@link ApiBinder#bindType(Class)} 的 to 方式也指定了实现类，那么代码方式会优先注解配置。
 * @see LinkedBindingBuilder
 * @version : 2015年7月28日
 * @author 赵永春 (zyc@hasor.net)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Documented
public @interface ImplBy {
    /**实现类。*/
    public Class<?> value();
}