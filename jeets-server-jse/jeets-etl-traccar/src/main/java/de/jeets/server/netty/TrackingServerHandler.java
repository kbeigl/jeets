package de.jeets.server.netty;

import de.jeets.protocol.JeetsGpsMessages.Acknowledge;
import de.jeets.protocol.JeetsGpsMessages.Position;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

	public class TrackingServerHandler extends SimpleChannelInboundHandler<Position> {

//		@Override	// messageReceived
	    public void channelRead0(ChannelHandlerContext ctx, Position position) throws Exception {
	        long currentTime = System.currentTimeMillis();

//	        logger !?
	        System.out.println("received position at " + currentTime 
	        		+ " :\n" + position.toString());

//	        validate position ...
	        boolean valid = true;
	        if (valid) {
//	        if (position.getValid()) {
//		        create return message{} according to received message{} 
	        	Acknowledge.Builder ackBuilder = Acknowledge.newBuilder();
	        	ackBuilder
	        	.setDeviceid(123)			// position.getDeviceid()
	        	.setMessageid(123456);		// position.getMessageid()
//	        	.setMessagetype( MsgType.ACK	);		// doesn't show in .toString ?!
//		        System.out.println("respond with:\n" + ackBuilder.toString());

		        ctx.write(ackBuilder.build());
		        System.out.println("responded with ack:\n" + ackBuilder.toString());
	        }
	        else
	        	return;		// like this? 
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
