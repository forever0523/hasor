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
package net.hasor.rsf;
import net.hasor.core.Environment;

import java.io.IOException;

/**
 * 序列化（编码/解码）器
 * @version : 2014年9月19日
 * @author 赵永春 (zyc@hasor.net)
 */
public interface SerializeCoder {
    /**初始化*/
    public void initCoder(Environment environment);

    /** decode byte[] to Object */
    public Object decode(byte[] bytes, Class<?> returnType) throws IOException;

    /** Encode Object to byte[] */
    public byte[] encode(Object object) throws IOException;
}