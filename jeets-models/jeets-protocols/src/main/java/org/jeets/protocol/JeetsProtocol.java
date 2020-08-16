package org.jeets.protocol;

import org.apache.camel.component.netty.NettyConsumer;
import org.apache.camel.component.netty.ServerInitializerFactory;
import org.apache.camel.component.netty.handlers.ServerChannelHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import org.jeets.protobuf.Jeets.Device;
import org.jeets.protocol.JeetsProtocol;

public class JeetsProtocol extends ServerInitializerFactory {

    /**
     * Override to implement Traccar.Acknowledge.Builder and send via
     * channel.writeAndFlush(..)
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // TODO Auto-generated method stub
        super.channelRead(ctx, msg); // comment ?

//      send ACK after successful transformation
//      if (deviceSession != null) {
//          if (channel != null) {
//              Traccar.Acknowledge.Builder ackBuilder = Traccar.Acknowledge.newBuilder();
//              ackBuilder.setDeviceid(123);
//              channel.writeAndFlush(new NetworkMessage(ackBuilder.build(), remoteAddress));
//              System.out.println("responded with ack:\n" + ackBuilder.toString());
//          }
//      }

    }

    private NettyConsumer consumer;

    public JeetsProtocol(NettyConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new ProtobufVarint32FrameDecoder());
        pipeline.addLast(new ProtobufDecoder(Device.getDefaultInstance()));
        pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
        pipeline.addLast(new ProtobufEncoder());
        pipeline.addLast(new ServerChannelHandler(consumer));          
    }

    @Override
    public ServerInitializerFactory createPipelineFactory(NettyConsumer consumer) {
        return new JeetsProtocol(consumer);
    }
}
