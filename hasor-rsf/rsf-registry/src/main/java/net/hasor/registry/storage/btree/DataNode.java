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
package net.hasor.registry.storage.btree;
/**
 * B-Tree 上的数据
 * @version : 2018年5月28日
 * @author 赵永春 (zyc@hasor.net)
 */
public class DataNode extends Node {
    public DataNode(long dataKey) {
        super(dataKey);
    }

    /** 返回是否为数据节点，DataNode 始终为 true */
    @Override
    public boolean isData() {
        return true;
    }
}