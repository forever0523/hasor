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
package net.hasor.dataql.domain;
/**
 * 结果集
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public interface DataModel {
    /** 得到本来面目 */
    public Object asOri();

    /** 解开 DataModel 包裹，采用 Map 和 List 封装。 */
    public Object unwrap();

    /** 判断是否为 ValueModel 类型值 */
    public default boolean isValue() {
        return false;
    }

    /** 判断是否为 ListModel 类型值 */
    public default boolean isList() {
        return false;
    }

    /** 判断是否为 ObjectModel 类型值 */
    public default boolean isObject() {
        return false;
    }

    /** 判断是否为 UdfModel 类型值 */
    public default boolean isUdf() {
        return false;
    }
}