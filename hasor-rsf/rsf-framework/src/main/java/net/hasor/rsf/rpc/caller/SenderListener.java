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
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.domain.RequestInfo;
import net.hasor.rsf.domain.ResponseInfo;
import net.hasor.rsf.rpc.net.SendCallBack;

/**
 *
 * @version : 2015年12月8日
 * @author 赵永春 (zyc@hasor.net)
 */
public interface SenderListener {
    /**向远端发送请求。*/
    public void sendRequest(InterAddress toAddress, RequestInfo info, SendCallBack callBack);

    /**向远端发送响应数据。*/
    public void sendResponse(InterAddress target, ResponseInfo info, SendCallBack callBack);
}