package org.jeets.tracker.netty;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.jeets.protocol.Traccar.Acknowledge;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * This handler can send all org.jeets.protocol.Traccar.<Object>s and
 * should always return an Acknowledge after successful transmission.
 *
 * @author kbeigl@jeets.org
 */
public class TraccarMessageHandler extends SimpleChannelInboundHandler<Acknowledge> {

    // Stateful properties
    private volatile Channel channel;

    private final BlockingQueue<Acknowledge> ackmsgs = new LinkedBlockingQueue<Acknowledge>();

    public TraccarMessageHandler() {
        super(false);
    }

    /**
     * Send any Object from Traccar Protocol class. Note that the receiver is
     * (currently) configured for a concrete class per host and port!
     * 
     * @param traccarObject
     * @return
     */
    public Acknowledge sendTraccarObject(Object traccarObject) {
//      TODO: validate traccarObject (belongs to Traccar protocol, extends GeneratedMessageV3)
        channel.writeAndFlush(traccarObject);
        Acknowledge ackmsg = waitForAcknowledge();
        return ackmsg;
    }
    
/*
    public Acknowledge sendTraccarPosition(Position position) {
        channel.writeAndFlush(position);
        Acknowledge ackmsg = waitForAcknowledge();
        return ackmsg;
    }

    public Acknowledge sendTraccarPositions(Positions positions) {
        channel.writeAndFlush(positions);
        Acknowledge ackmsg = waitForAcknowledge();
        return ackmsg;
    }

    public Acknowledge sendTraccarDevice(Device device) {
        channel.writeAndFlush(device);
        Acknowledge ackmsg = waitForAcknowledge();
        return ackmsg;
    }
 */

    /**
	 * After sending a message the server should return an Acknowledge message
	 * for a valid transmission. This method is waiting for the callback on
	 * the channelRead0 method.
	 * 
	 * @param ackmsg
	 * @return
	 */
//	TODO: add timeout mechanism
	private Acknowledge waitForAcknowledge( /* maximumTimeToWait, i.e. timeout */ ) {
		boolean interrupted = false;
		Acknowledge ackMsg;
        for (;;) {
            try {
            	ackMsg = ackmsgs.take();
            	break;
            } catch (InterruptedException ignore) {
                interrupted = true;
            }
        }
		if (interrupted) {
			Thread.currentThread().interrupt();
			return null;
		}
		return ackMsg;
	}

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        channel = ctx.channel();
    }
    
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Acknowledge ack) throws Exception {
    	ackmsgs.add(ack);	// release blocking Q > continue send* method
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
