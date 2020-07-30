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
package net.hasor.tconsole.launcher.telnet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.hasor.tconsole.launcher.TelSessionObject;
import net.hasor.tconsole.launcher.TelUtils;
import net.hasor.tconsole.spi.TelSessionCreateListener;
import net.hasor.tconsole.spi.TelSessionDestroyListener;
import net.hasor.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Predicate;

import static net.hasor.tconsole.TelOptions.ENDCODE_OF_SILENT;
import static net.hasor.tconsole.TelOptions.SILENT;
import static net.hasor.tconsole.launcher.AbstractTelService.CMD;

/**
 * Handles a server-side channel.
 * @version : 2016年09月20日
 * @author 赵永春 (zyc@hasor.net)
 */
@ChannelHandler.Sharable
class TelNettyHandler extends SimpleChannelInboundHandler<String> {
    protected static Logger            logger         = LoggerFactory.getLogger(TelNettyHandler.class);
    private          Predicate<String> inBoundMatcher = null;
    private          TelnetTelService  telContext     = null; // 环境
    private          TelSessionObject  telSession     = null; // 会话
    private          ByteBuf           dataReader     = null; // 读取缓冲

    TelNettyHandler(TelnetTelService telContext, Predicate<String> inBoundMatcher) {
        this.inBoundMatcher = inBoundMatcher == null ? s -> true : inBoundMatcher;
        this.telContext = telContext;
    }

    @Override
    public boolean acceptInboundMessage(Object msg) {
        return telSession != null && !telSession.isClose();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        InetSocketAddress inetAddress = (InetSocketAddress) channel.remoteAddress();
        String remoteAddress = inetAddress.getAddress().getHostAddress();
        //
        // .不允许连入的情况
        if (!this.inBoundMatcher.test(remoteAddress)) {
            logger.warn("tConsole -> reject inBound socket ,remoteAddress = {}.", remoteAddress);
            channel.write("--------------------------------------------\r\n\r\n");
            channel.write("I'm sorry you are not allowed to connect tConsole.\r\n\r\n");
            channel.write(" your address is :" + remoteAddress + "\r\n");
            channel.write("--------------------------------------------\r\n");
            channel.flush();
            channel.close();
            return;
        }
        logger.info("tConsole -> accept inBound socket ,remoteAddress = {}.", remoteAddress);
        //
        // .构造会话
        TelNettyWriter dataWriter = new TelNettyWriter(channel);
        this.dataReader = this.telContext.getByteBufAllocator().heapBuffer();
        this.telSession = new TelSessionObject(this.telContext, dataReader, dataWriter) {
            public boolean isClose() {
                return dataWriter.isClose();
            }
        };
        //
        // .异步延迟 200ms 打印欢迎信息
        this.telContext.asyncExecute(() -> {
            try {
                Thread.sleep(200);
                printWelcome(channel);
            } catch (Exception e) { /**/ }
        });
        //
        // .创建Session
        logger.info("tConsole -> trigger TelSessionListener.sessionCreated");
        this.telContext.getSpiTrigger().notifySpiWithoutResult(TelSessionCreateListener.class, listener -> {
            listener.sessionCreated(this.telSession);
        });
    }

    private void printWelcome(Channel channel) {
        if (TelUtils.aBoolean(this.telSession, SILENT)) {
            logger.info("tConsole -> silent, ignore Welcome info.");
            return;
        }
        logger.info("tConsole -> send Welcome info.");
        // Send greeting for a new connection.
        channel.write("--------------------------------------------\r\n\r\n");
        channel.write("Welcome to tConsole!\r\n");
        channel.write("\r\n");
        channel.write("     login : " + new Date() + " now. form " + channel.remoteAddress() + "\r\n");
        channel.write("    workAt : " + channel.localAddress() + "\r\n");
        channel.write("Tips: You can enter a 'help' or 'help -a' for more information.\r\n");
        channel.write("use the 'exit' or 'quit' out of the console.\r\n");
        channel.write("--------------------------------------------\r\n");
        channel.write(CMD);
        channel.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        // .数据丢入缓冲区缓冲区，然后尝试执行一次。
        this.dataReader.writeCharSequence(msg + "\n", StandardCharsets.UTF_8);// 加 /n 是由于用了 DelimiterBasedFrameDecoder
        int lastBufferSize = this.telSession.buffSize();
        //
        while (this.telSession.tryReceiveEvent()) {
            if (lastBufferSize == this.telSession.buffSize()) {
                break;
            }
            lastBufferSize = this.telSession.buffSize();
        }
        //
        if (this.telSession.buffSize() == 0) {
            boolean noSilent = !TelUtils.aBoolean(this.telSession, SILENT);
            if (noSilent) {
                ctx.channel().writeAndFlush(CMD);
            } else {
                String codeOfSilent = TelUtils.aString(this.telSession, ENDCODE_OF_SILENT);
                if (StringUtils.isNotBlank(codeOfSilent)) {
                    ctx.channel().writeAndFlush(codeOfSilent + "\n");// 加 /n 是由于用了 DelimiterBasedFrameDecoder
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("tConsole error->" + cause.getMessage(), cause);
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.info("tConsole -> trigger TelSessionDestroyListener.sessionDestroyed");
        this.telContext.getSpiTrigger().notifySpiWithoutResult(TelSessionDestroyListener.class, listener -> {
            listener.sessionDestroyed(this.telSession); // .销毁Session
        });
    }
}