package org.jeets.dcsToAmq;

import org.apache.camel.component.netty.ClientInitializerFactory;
import org.apache.camel.component.netty.NettyProducer;
import org.apache.camel.component.netty.handlers.ClientChannelHandler;
import org.jeets.protobuf.Jeets;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

public class AckProtoExtractor extends ClientInitializerFactory {
    private NettyProducer producer;

    public AckProtoExtractor(NettyProducer producer) {
        this.producer = producer;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline channelPipeline = ch.pipeline();
        channelPipeline.addLast(new ProtobufVarint32FrameDecoder());
        channelPipeline.addLast(new ProtobufDecoder(Jeets.Acknowledge.getDefaultInstance()));
        channelPipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
        channelPipeline.addLast(new ProtobufEncoder());
        channelPipeline.addLast("handler", new ClientChannelHandler(producer));
    }

    @Override
    public ClientInitializerFactory createPipelineFactory(NettyProducer producer) {
        return new AckProtoExtractor(producer);
    }
    
}
