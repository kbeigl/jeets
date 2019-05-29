/*
 * Copyright 2012 - 2018 Anton Tananaev (anton@traccar.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.timeout.IdleStateHandler;

import org.apache.camel.component.netty4.NettyConsumer;
import org.apache.camel.component.netty4.ServerInitializerFactory;
import org.apache.camel.component.netty4.handlers.ServerChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.traccar.events.AlertEventHandler;
import org.traccar.events.CommandResultEventHandler;

import java.net.InetSocketAddress;
import java.util.Map;

public abstract class BasePipelineFactory extends ServerInitializerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasePipelineFactory.class);

    private final TrackerServer server;
    private int timeout;

    private AlertEventHandler alertEventHandler;
    private RemoteAddressHandler remoteAddressHandler;
    private CommandResultEventHandler commandResultEventHandler;

    private static final class OpenChannelHandler extends ChannelDuplexHandler {

        private final TrackerServer server;

        private OpenChannelHandler(TrackerServer server) {
            this.server = server;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
            server.getChannelGroup().add(ctx.channel());
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            super.channelInactive(ctx);
            server.getChannelGroup().remove(ctx.channel());
        }

    }

    private static class NetworkMessageHandler extends ChannelDuplexHandler {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (ctx.channel() instanceof DatagramChannel) {
                DatagramPacket packet = (DatagramPacket) msg;
                ctx.fireChannelRead(new NetworkMessage(packet.content(), packet.sender()));
            } else {
                ByteBuf buffer = (ByteBuf) msg;
                ctx.fireChannelRead(new NetworkMessage(buffer, ctx.channel().remoteAddress()));
            }
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            NetworkMessage message = (NetworkMessage) msg;
            if (ctx.channel() instanceof DatagramChannel) {
                InetSocketAddress recipient = (InetSocketAddress) message.getRemoteAddress();
                InetSocketAddress sender = (InetSocketAddress) ctx.channel().localAddress();
                ctx.write(new DatagramPacket((ByteBuf) message.getMessage(), recipient, sender), promise);
            } else {
                ctx.write(message.getMessage(), promise);
            }
        }

    }

    private static class StandardLoggingHandler extends ChannelDuplexHandler {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            log(ctx, false, msg);
            super.channelRead(ctx, msg);
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
//          NettyConsumer always triggers NettyHelper.writeBodyAsync(..)
//          and we don't want to send responses in addition to Ack
            try {
                NetworkMessage networkMessage = (NetworkMessage) msg;
            } catch (Exception e) {
//              System.err.println("No NetworkMessage - don't write response.");
                return;
            }
            log(ctx, true, msg);
            super.write(ctx, msg, promise);
        }

//      always expecting a NetworkMessage
        public void log(ChannelHandlerContext ctx, boolean downstream, Object o) {
            NetworkMessage networkMessage = (NetworkMessage) o;
            StringBuilder message = new StringBuilder();

            message.append("[").append(ctx.channel().id().asShortText()).append(": ");
            message.append(((InetSocketAddress) ctx.channel().localAddress()).getPort());
            if (downstream) {
                message.append(" > ");
            } else {
                message.append(" < ");
            }

            if (networkMessage.getRemoteAddress() != null) {
                message.append(((InetSocketAddress) networkMessage.getRemoteAddress()).getHostString());
            } else {
                message.append("null");
            }
            message.append("]");

            message.append(" HEX: ");
            message.append(ByteBufUtil.hexDump((ByteBuf) networkMessage.getMessage()));

            LOGGER.info(message.toString());
        }

    }

    /* construct BPF for registration only, i.e. without NettyConsumer */
    public BasePipelineFactory(TrackerServer server, String protocol) {
        this.server = server;
        timeout = 0;
        timeout = Context.getConfig().getInteger(protocol + ".timeout");
        if (timeout == 0) {
            timeout = Context.getConfig().getInteger(protocol + ".resetDelay"); // temporary
            if (timeout == 0) {
                timeout = Context.getConfig().getInteger("server.timeout");
            }
        }
        if (Context.getConfig().getBoolean("processing.remoteAddress.enable")) {
            remoteAddressHandler = new RemoteAddressHandler();
        }
        if (Context.getConfig().getBoolean("event.enable")) {
            commandResultEventHandler = new CommandResultEventHandler();
            alertEventHandler = new AlertEventHandler();
        }
    }

    protected abstract void addProtocolHandlers(PipelineBuilder pipeline);

    private void addHandlers(ChannelPipeline pipeline, ChannelHandler... handlers) {
        for (ChannelHandler handler : handlers) {
            if (handler != null) {
                pipeline.addLast(handler);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends ChannelHandler> T getHandler(ChannelPipeline pipeline, Class<T> clazz) {
        for (Map.Entry<String, ChannelHandler> handlerEntry : pipeline) {
            ChannelHandler handler = handlerEntry.getValue();
            if (handler instanceof WrapperInboundHandler) {
                handler = ((WrapperInboundHandler) handler).getWrappedHandler();
            } else if (handler instanceof WrapperOutboundHandler) {
                handler = ((WrapperOutboundHandler) handler).getWrappedHandler();
            }
            if (clazz.isAssignableFrom(handler.getClass())) {
                return (T) handler;
            }
        }
        return null;
    }

    @Override
    protected void initChannel(Channel channel) throws Exception {
        final ChannelPipeline pipeline = channel.pipeline();
        if (timeout > 0 && !server.isDatagram()) {
            pipeline.addLast(new IdleStateHandler(timeout, 0, 0));
        }
        pipeline.addLast(new OpenChannelHandler(server));
//      Begin NetworkMessage ------------------------------
        pipeline.addLast(new NetworkMessageHandler());
        pipeline.addLast(new StandardLoggingHandler());

        addProtocolHandlers(new PipelineBuilder() {
            @Override
            public void addLast(ChannelHandler handler) {
                if (!(handler instanceof BaseProtocolDecoder || handler instanceof BaseProtocolEncoder)) {
                    if (handler instanceof ChannelInboundHandler) {
                        handler = new WrapperInboundHandler((ChannelInboundHandler) handler);
                    } else {
                        handler = new WrapperOutboundHandler((ChannelOutboundHandler) handler);
                    }
                }
                pipeline.addLast(handler);
            }
        });
//      End NetworkMessage --------------------------------
//      ExtendedObjectDecoder fires every single decodedMessage

//      regular ChannelInboundHandlerAdapter
        addHandlers(pipeline, remoteAddressHandler);
//      simply fires (passes on?) the Position
        pipeline.addLast(new DefaultDataHandler()); // can be skipped
//      ext BaseDataHandler ext ChannelInboundHandlerAdapter

//      add two BaseEventHandlers
        addHandlers(pipeline, commandResultEventHandler, alertEventHandler);
//      log netty events and Position output (replace with ServerInitializerFactory!?)
        pipeline.addLast(new MainEventHandler()); // i.e. NettyEventHandler
//      dead end

//      add Camel Endpoint IF invoked in camel-netty4 from registry
        if (consumer != null) {
            System.out.println("add nettyHandler");
            pipeline.addLast("nettyHandler", new ServerChannelHandler(consumer));
        }
    }

    @Override
    public ServerInitializerFactory createPipelineFactory(NettyConsumer nettyConsumer) {

        System.out.println("createPipelineFactory for " + nettyConsumer);

        @SuppressWarnings("rawtypes")
        Class protocolClass = server.getProtocolClass();
        if (BaseProtocol.class.isAssignableFrom(protocolClass)) {
            try {
                BaseProtocol protocol = (BaseProtocol) protocolClass.newInstance();
//              can be more than one server! protocol.getServerList();
//              TO DO: only return first for now
                BasePipelineFactory bpf = protocol.getServerList().iterator().next().getPipelineFactory();
//              NettyConsumer not part of the construction hierarchy
//              as original *Protocol() constructor can not be modified
                bpf.setNettyConsumer(nettyConsumer);
                return (ServerInitializerFactory) bpf;
//              return bpf;
            } catch (InstantiationException | IllegalAccessException e) {
//              now what?
                e.printStackTrace();
            }
        }
//      null throws java.lang.NullPointerException: childHandler
//      at io.netty.bootstrap.ServerBootstrap.childHandler(ServerBootstrap.java:134)
        return null;
    }

    private NettyConsumer consumer;
    private void setNettyConsumer(NettyConsumer nettyConsumer) {
        consumer = nettyConsumer;
    }

}
