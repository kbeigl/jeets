/*
 * Copyright 2015 Anton Tananaev (anton.tananaev@gmail.com)
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
package org.traccar.protocol;

import java.util.List;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.protobuf.ProtobufDecoder;
import org.jboss.netty.handler.codec.protobuf.ProtobufEncoder;
import org.jboss.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import org.jboss.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
//import org.jboss.netty.handler.ssl.SslContext;
import org.traccar.BaseProtocol;
import org.traccar.TrackerServer;
import org.jeets.protocol.Traccar.Device;

public class ProtobufferDeviceProtocol extends BaseProtocol {

    /* private final SslContext sslCtx;
     * public ProtobufferDeviceProtocol(SslContext sslCtx)
     * { super("pb.device"); this.sslCtx = sslCtx; }
     */

    public ProtobufferDeviceProtocol() {
        super("pb.device");
    }

    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {
        serverList.add(new TrackerServer(new ServerBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("pb-frameDecoder", new ProtobufVarint32FrameDecoder());
                // Decode/Encode a received 3.x ChannelBuffer (= 4.x ByteBuf)
                // into a Google Protocol Buffers Message and MessageLite.
//              pipeline.addLast("pb-pos-decoder", new ProtobufDecoder(Position.getDefaultInstance()));
                pipeline.addLast("pb-dev-decoder", new ProtobufDecoder(Device.getDefaultInstance()));
                pipeline.addLast("pb-fieldPrepender", new ProtobufVarint32LengthFieldPrepender());
                pipeline.addLast("pb-encoder", new ProtobufEncoder());
                pipeline.addLast("pb-decoder", new ProtobufferDeviceDecoder(ProtobufferDeviceProtocol.this));
            }
        });
    }

}
