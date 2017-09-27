/*
  * Copyright 2017 The Jee Tracking System
  * Copyright 2017 Kristof Beiglböck kbeigl@roaf.de
  *
  * The JeeTS Project licenses this file to you under the Apache License,
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
package de.jeets.server.netty;

import de.jeets.protocol.JeetsGpsMessages.Position;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
//import io.netty.handler.ssl.SslContext;

public class TrackingServerInitializer extends ChannelInitializer<SocketChannel> {

//	private final SslContext sslCtx;
//	public TrackingServerInitializer(SslContext sslCtx) {
//		this.sslCtx = sslCtx;
//	}

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
//      if (sslCtx != null) {
//      	pipeline.addLast(sslCtx.newHandler(ch.alloc()));
//      }
//      BEFORE DECODE --> AFTER DECODE
        pipeline.addLast(new ProtobufVarint32FrameDecoder());
//      Decodes a received ByteBuf into a Google Protocol Buffers Message and MessageLite.
        pipeline.addLast(new ProtobufDecoder(Position.getDefaultInstance()));
//      BEFORE ENCODE --> AFTER ENCODE 
        pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
//      Encodes the requested Google Protocol Buffers Message and MessageLite into a ByteBuf.
        pipeline.addLast(new ProtobufEncoder());
//      ChannelInboundHandlerAdapter which allows to explicit only handle a specific type of messages.
        pipeline.addLast(new TrackingServerHandler());
    }
}
