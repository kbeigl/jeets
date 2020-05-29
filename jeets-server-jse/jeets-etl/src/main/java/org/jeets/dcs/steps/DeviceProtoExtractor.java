package org.jeets.dcs.steps;

import org.apache.camel.component.netty.NettyConsumer;
import org.apache.camel.component.netty.ServerInitializerFactory;
import org.apache.camel.component.netty.handlers.ServerChannelHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import org.jeets.protobuf.Traccar;

public class DeviceProtoExtractor extends ServerInitializerFactory {
    private NettyConsumer consumer;

    public DeviceProtoExtractor(NettyConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline channelPipeline = ch.pipeline();
        channelPipeline.addLast(new ProtobufVarint32FrameDecoder());
        channelPipeline.addLast(new ProtobufDecoder(Traccar.Device.getDefaultInstance()));
        channelPipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
        channelPipeline.addLast(new ProtobufEncoder());
        channelPipeline.addLast(new ServerChannelHandler(consumer));          
    }

    @Override
    public ServerInitializerFactory createPipelineFactory(NettyConsumer consumer) {
        return new DeviceProtoExtractor(consumer);
    }
}
