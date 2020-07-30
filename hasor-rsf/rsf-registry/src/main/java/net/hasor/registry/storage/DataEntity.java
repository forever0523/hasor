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
package net.hasor.registry.storage;
/**
 *
 * @version : 2015年8月19日
 * @author 赵永春 (zyc@hasor.net)
 */
public class DataEntity {
    private String dataKey;
    private long   tags;
    //
    private long   dataPosition;
    private long   dataLength;

    //
    //
    public String getDataKey() {
        return dataKey;
    }

    public String getDataValue() {
        return null;
    }

    public long getTags() {
        return tags;
    }

    public String getMD5() {
        return null;
    }

    public String getTarget() {
        return null;
    }

    public boolean isLinkTo() {
        return false;
    }
}
