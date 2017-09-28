package org.jeets.tracker;

import org.jeets.protocol.Traccar;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class TraccarServerHandler extends SimpleChannelInboundHandler<Traccar.Device> {

//  @Override   // messageReceived                      message/S{}
    public void channelRead0(ChannelHandlerContext ctx, Traccar.Device device) throws Exception {
        long currentTime = System.currentTimeMillis();
//      logger !?
        System.out.println("received device at " + currentTime + " :\n" + device.toString());
        Traccar.Acknowledge.Builder ackBuilder = Traccar.Acknowledge.newBuilder();
        ackBuilder.setDeviceid(123);
        ctx.write(ackBuilder.build());
        System.out.println("responded with ack:\n" + ackBuilder.toString());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
