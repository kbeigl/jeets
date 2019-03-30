package org.jeets.routes;

import org.apache.camel.component.netty4.ClientInitializerFactory;
import org.apache.camel.component.netty4.NettyProducer;
import org.apache.camel.component.netty4.handlers.ClientChannelHandler;
import org.jeets.protocol.Traccar;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

/* only for testing with netty producer */
public class ClientInitializer extends ClientInitializerFactory {
    private NettyProducer producer;

    public ClientInitializer(NettyProducer producer) {
        this.producer = producer;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline channelPipeline = ch.pipeline();
        //  in - decode
        channelPipeline.addLast(new ProtobufVarint32FrameDecoder());
        channelPipeline.addLast(new ProtobufDecoder(Traccar.Acknowledge.getDefaultInstance()));
        // out - encode
        channelPipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
        channelPipeline.addLast(new ProtobufEncoder());
        // in
        channelPipeline.addLast("handler", new ClientChannelHandler(producer));
    }

    @Override
    public ClientInitializerFactory createPipelineFactory(NettyProducer producer) {
        return new ClientInitializer(producer);
    }

}
