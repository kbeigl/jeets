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

//import io.netty.buffer.ByteBuf;
//import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
//import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
//import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPipeline;
//import io.netty.channel.ChannelPromise;
//import io.netty.channel.socket.DatagramChannel;
//import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.timeout.IdleStateHandler;

import org.apache.camel.component.netty4.NettyConsumer;
import org.apache.camel.component.netty4.ServerInitializerFactory;
import org.apache.camel.component.netty4.handlers.ServerChannelHandler;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.traccar.handler.DefaultDataHandler;
import org.traccar.handler.NetworkMessageHandler;
import org.traccar.handler.OpenChannelHandler;
import org.traccar.handler.RemoteAddressHandler;
import org.traccar.handler.StandardLoggingHandler;

import java.util.Map;

public abstract class BasePipelineFactory extends ServerInitializerFactory {

//  private static final Logger LOGGER = LoggerFactory.getLogger(BasePipelineFactory.class);

    private final TrackerServer server;
    private final String protocol;
    private int timeout;

    private RemoteAddressHandler remoteAddressHandler;

    /* construct BPF for registration only, i.e. without NettyConsumer */
    public BasePipelineFactory(TrackerServer server, String protocol) {
        this.server = server;
        this.protocol = protocol;
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
    }

    protected abstract void addProtocolHandlers(PipelineBuilder pipeline);

    private void addHandlers(ChannelPipeline pipeline, ChannelHandler... handlers) {
        for (ChannelHandler handler : handlers) {
            if (handler != null) {
                pipeline.addLast(handler);
            }
        }
    }

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
    protected void initChannel(Channel channel) {
        final ChannelPipeline pipeline = channel.pipeline();

        if (timeout > 0 && !server.isDatagram()) {
            pipeline.addLast(new IdleStateHandler(timeout, 0, 0));
        }
        pipeline.addLast(new OpenChannelHandler(server));
//      Begin NetworkMessage ------------------------------
        pipeline.addLast(new NetworkMessageHandler());
        pipeline.addLast(new StandardLoggingHandler(protocol));

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
