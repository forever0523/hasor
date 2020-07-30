/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.tconsole;
/**
 * tConsole 指令。
 * @version : 2016年09月20日
 * @author 赵永春 (zyc@hasor.net)
 */
public interface TelExecutor {
    /** 帮助信息 */
    public default String helpInfo() {
        return "no more information.";
    }

    /** 用于确定读取一个完整的命令 */
    public default boolean readCommand(TelCommand telCommand, TelReader telReader) {
        return true;
    }

    /**执行命令*/
    public String doCommand(TelCommand telCommand) throws Throwable;
}