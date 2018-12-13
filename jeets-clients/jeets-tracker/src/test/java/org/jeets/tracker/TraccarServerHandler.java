package org.jeets.tracker;

import org.jeets.protocol.Traccar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class TraccarServerHandler extends SimpleChannelInboundHandler<Traccar.Device> {

	private static final Logger log = LoggerFactory.getLogger(TraccarServerHandler.class);

//  @Override   // messageReceived                      message/S{}
    public void channelRead0(ChannelHandlerContext ctx, Traccar.Device device) throws Exception {
        long currentTime = System.currentTimeMillis();
        log.info("received device at {} :\n{}", currentTime, device.toString());
        Traccar.Acknowledge.Builder ackBuilder = Traccar.Acknowledge.newBuilder();
        ackBuilder.setDeviceid(123);
        ctx.write(ackBuilder.build());
        log.info("responded with ack:\n{}", ackBuilder.toString());
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
