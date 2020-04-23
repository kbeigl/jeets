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

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPipeline;
//import io.netty.channel.socket.DatagramChannel;
//import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.timeout.IdleStateHandler;

import org.apache.camel.component.netty.NettyConsumer;
import org.apache.camel.component.netty.ServerInitializerFactory;
import org.apache.camel.component.netty.handlers.ServerChannelHandler;

import org.traccar.handler.NetworkMessageHandler;
import org.traccar.handler.OpenChannelHandler;
import org.traccar.handler.RemoteAddressHandler;
import org.traccar.handler.StandardLoggingHandler;

import java.util.Map;

public abstract class BasePipelineFactory extends ChannelInitializer<Channel> {

//  private static final Logger LOGGER = LoggerFactory.getLogger(BasePipelineFactory.class);
    private final TrackerServer server;
    private final String protocol;
    private int timeout;
//  Shared Handler/s
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
//      ignore return value
        initTraccarPipeline(channel);
    }

    /**
     * This BasePipelineFactory is the original construct to initialize the Traccar
     * pipeline. The CamelPipelineFactory provides a separate implementation with an
     * additional Netty Consumer to pick up the entities at the end of the pipeline.
     * The CamelPipelineFactory can be provided to a Spring @Configuration file to
     * trigger createPipelineFactory(NettyConsumer) and apply the
     * ServerInitializerFactory in a camel-netty route.
     */
    class CamelPipelineFactory extends ServerInitializerFactory {
        private NettyConsumer consumer;

        /**
         * Create BasePipelineFactory for NettyConsumer, i.e. CamelPipelineFactory
         */
        public CamelPipelineFactory(NettyConsumer consumer) {
            this.consumer = consumer;
        }

        /**
         * Initialize CamelPipeline with NettyConsumer once connection channel is
         * established. The CamelPipeline simply appends the Camel endpoint to the
         * original BasePipeline.
         */
        @Override
        protected void initChannel(Channel channel) throws Exception {
//          log.info("append nettyHandler to Traccar pipeline");
            initTraccarPipeline(channel)
            .addLast("nettyHandler", new ServerChannelHandler(consumer));          
        }

        /**
         * Create CamelPipeline to be registered as ServerInitializerFactory in a Camel
         * route.
         */
        @Override
        public ServerInitializerFactory createPipelineFactory(NettyConsumer consumer) {
            return new CamelPipelineFactory(consumer);
        }
    }

    /**
     * Initialize Traccar Pipeline without camel-netty when a new channel is
     * established. Ignoring the return value creates the original Traccar pipeline.
     * In order to connect a NettyConsumer the returned pipeline can be enriched
     * with a ServerChannelHandler for a ServerInitializerFactory.
     */
    private ChannelPipeline initTraccarPipeline(Channel channel) {
        final ChannelPipeline pipeline = channel.pipeline();
        if (timeout > 0 && !server.isDatagram()) {
            pipeline.addLast(new IdleStateHandler(timeout, 0, 0));
        }
        pipeline.addLast(new OpenChannelHandler(server));
//      Begin NetworkMessage ------------------------------
        pipeline.addLast(new NetworkMessageHandler());
        pipeline.addLast(new StandardLoggingHandler(protocol));
        addProtocolHandlers(new PipelineBuilder() { // from TrackerServer, i.e. protocol specific
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
        addHandlers(pipeline, remoteAddressHandler);
        pipeline.addLast(new MainEventHandler());
        return pipeline;
    }

    /* Create a consumer linked channel pipeline factory */
    @Deprecated
    public ServerInitializerFactory createPipelineFactory(NettyConsumer nettyConsumer) {
        System.err.println("createPipelineFactory for " + nettyConsumer + " DEactivated !!!");
        return null;
    }

}
