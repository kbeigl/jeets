package org.jeets.dcx.steps;

import org.apache.camel.component.netty.NettyConsumer;
import org.apache.camel.component.netty.ServerInitializerFactory;
import org.apache.camel.component.netty.handlers.ServerChannelHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import org.jeets.dcx.steps.DeviceProtoExtractor;
import org.jeets.protobuf.Traccar;

public class DeviceProtoExtractor extends ServerInitializerFactory {
    private NettyConsumer consumer;

    public DeviceProtoExtractor(NettyConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new ProtobufVarint32FrameDecoder());
        pipeline.addLast(new ProtobufDecoder(Traccar.Device.getDefaultInstance()));
        pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
        pipeline.addLast(new ProtobufEncoder());
        pipeline.addLast(new ServerChannelHandler(consumer));          
    }

    @Override
    public ServerInitializerFactory createPipelineFactory(NettyConsumer consumer) {
        return new DeviceProtoExtractor(consumer);
    }
}
