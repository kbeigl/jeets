/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.jeets.tracker.netty;

import org.jeets.protocol.Traccar.Acknowledge;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.ssl.SslContext;

/**
 * Initialize channel to receive an Traccar Acknowledge message for every
 * Traccar Object successfully transmitted to a receiver.
 * 
 * @author kbeigl@jeets.org
 */
public class TraccarAckInitializer<T extends Channel> extends ChannelInitializer<T> {

    private final SslContext sslCtx;
    private final String host;
    private final int port;

    public TraccarAckInitializer(SslContext sslCtx, String host, int port) {
        this.sslCtx = sslCtx;
        this.host = host;
        this.port = port;
    }

    @Override
    public void initChannel(T channel) {
        ChannelPipeline p = ((Channel) channel).pipeline();
        if (sslCtx != null) {
            p.addLast(sslCtx.newHandler(((Channel) channel).alloc(), host, port));
        }
//      p.addLast(new LoggingHandler(LogLevel.INFO));
        p.addLast(new ProtobufVarint32FrameDecoder());
        p.addLast(new ProtobufDecoder(Acknowledge.getDefaultInstance()));
        p.addLast(new ProtobufVarint32LengthFieldPrepender());
        p.addLast(new ProtobufEncoder());
//      handler for all Traccar.Objects
        p.addLast(new TraccarMessageHandler());
    }
}
