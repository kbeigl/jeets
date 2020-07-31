/*
 * Copyright 2012 - 2019 Anton Tananaev (anton@traccar.org)
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
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.traccar.config.Keys;
import org.traccar.handler.NetworkMessageHandler;
import org.traccar.handler.StandardLoggingHandler;

import java.util.Map;

import org.apache.camel.component.netty.NettyConsumer;
import org.apache.camel.component.netty.ServerInitializerFactory;
import org.apache.camel.component.netty.handlers.ServerChannelHandler;

public abstract class BasePipelineFactory extends ChannelInitializer<Channel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasePipelineFactory.class);

    private final TrackerServer server;
    private final String protocol;
    private int timeout;

    /* construct BPF for registration only, i.e. without NettyConsumer */
    public BasePipelineFactory(TrackerServer server, String protocol) {
        this.server = server;
        this.protocol = protocol;
        timeout = Context.getConfig().getInteger(Keys.PROTOCOL_TIMEOUT.withPrefix(protocol));
        if (timeout == 0) {
            timeout = Context.getConfig().getInteger(Keys.SERVER_TIMEOUT);
        }
    }

    protected abstract void addProtocolHandlers(PipelineBuilder pipeline);

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
            LOGGER.debug("append nettyHandler to Traccar pipeline");
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
//        pipeline.addLast(new OpenChannelHandler(server)); // remove?
        pipeline.addLast(new NetworkMessageHandler()); // begin
        pipeline.addLast(new StandardLoggingHandler(protocol));
        addProtocolHandlers(handler -> {
            if (!(handler instanceof BaseProtocolDecoder || handler instanceof BaseProtocolEncoder)) {
                if (handler instanceof ChannelInboundHandler) {
                    handler = new WrapperInboundHandler((ChannelInboundHandler) handler);
                } else {
                    handler = new WrapperOutboundHandler((ChannelOutboundHandler) handler);
                }
            }
            pipeline.addLast(handler);
        }); // end NetworkMessage
        pipeline.addLast(new MainEventHandler());
        return pipeline;
    }

}
