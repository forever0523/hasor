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
package net.hasor.core.spi;
import net.hasor.core.AppContext;

/**
 * 用于容器销毁事件接收
 * @version : 2014-5-10
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ContextShutdownListener extends java.util.EventListener {
    /**开始进入容器销毁过程。*/
    public void doShutdown(AppContext appContext);

    /**容器销毁完成。*/
    public void doShutdownCompleted(AppContext appContext);
}