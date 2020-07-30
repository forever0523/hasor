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
package net.hasor.rsf.protocol.rsf;
import io.netty.channel.ChannelHandler;
import net.hasor.core.AppContext;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.protocol.rsf.v1.PoolBlock;
import net.hasor.rsf.rpc.net.Connector;
import net.hasor.rsf.rpc.net.netty.ProtocolHandlerFactory;
import net.hasor.rsf.rpc.net.netty.RsfDuplexHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RSF 解码器
 * @version : 2014年10月10日
 * @author 赵永春 (zyc@hasor.net)
 */
public class RsfProtocolHandler implements ProtocolHandlerFactory {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public ChannelHandler[] channelHandler(Connector connector, AppContext appContext) {
        RsfEnvironment env = appContext.getInstance(RsfEnvironment.class);
        RsfDuplexHandler duplexHandler = new RsfDuplexHandler(  //
                new RsfDecoder(env, PoolBlock.DataMaxSize, appContext.getClassLoader()),//
                new RsfEncoder(env, appContext.getClassLoader())//
        );
        return new ChannelHandler[] {                           //
                duplexHandler,                                  //
                new ShakeHands(connector)                       //
        };
    }
}