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
package test.net.hasor.rsf.alone;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfModule;
import net.hasor.rsf.RsfResult;
import test.net.hasor.rsf.services.EchoService;
import test.net.hasor.rsf.services.MessageService;

/**
 *
 * @version : 2014年9月12日
 * @author 赵永春 (zyc@hasor.net)
 */
public class CustomerClient {
    public static void main(String[] args) throws Throwable {
        //Client
        AppContext clientContext = Hasor.create().mainSettingWith("/alone/customer-config.xml").build((RsfModule) apiBinder -> {
            InterAddress localRsf = new InterAddress("rsf://localhost:2181/default");
            //            InterAddress localHprose = null;// new InterAddress("http://127.0.0.1:2181/default");
            apiBinder.rsfService(EchoService.class).bindAddress(localRsf, localRsf).register();
            apiBinder.rsfService(MessageService.class).bindAddress(localRsf, localRsf).register();
        });
        System.out.println("server start.");
        //
        //Client -> Server
        RsfClient client = clientContext.getInstance(RsfClient.class);
        EchoService echoService = client.wrapper(EchoService.class);
        for (int i = 0; i < 20; i++) {
            try {
                String res = echoService.sayHello("Hello Word for Invoker");
                System.out.println("invoker -> " + res);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //
        MessageService messageService = client.wrapper(MessageService.class);
        for (int i = 0; i < 20; i++) {
            try {
                RsfResult res = messageService.sayHello("Hello Word for Message.");
                System.out.println("message -> " + res.isSuccess());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.in.read();
    }
}