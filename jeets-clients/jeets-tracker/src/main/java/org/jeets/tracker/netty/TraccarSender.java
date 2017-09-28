package org.jeets.tracker.netty;

import javax.net.ssl.SSLException;

import org.jeets.protocol.Traccar.Acknowledge;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

/**
 * The TraccarClient is responsible to send Protobuffer Messages from the
 * Traccar protocol class and receive an Acknowledge to be used for cleaning up
 * the local database.
 * 
 * @author kbeigl@jeets.org
 */
public class TraccarSender {

    static final boolean SSL = System.getProperty("ssl") != null;

	/**
	 * @param protobuffer position 
	 * @throws SSLException
	 * @throws InterruptedException
	 */
    public static Acknowledge transmitTraccarObject(Object traccarObject, String host, int port) {
	    
//      TODO: check if traccarObject belongs to Traccar protocol and <T extends GeneratedMessageV3>
        
        final SslContext sslCtx = setSslContext();
        Acknowledge ack;
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .handler(new TraccarAckInitializer<SocketChannel>(sslCtx, host, port));

            Channel ch = b.connect(host, port).sync().channel();
            TraccarMessageHandler handler = ch.pipeline().get(TraccarMessageHandler.class);
            ack = handler.sendTraccarObject(traccarObject);
            ch.close();

        } catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		} finally {
            group.shutdownGracefully();
        }
		return ack;
	}

    private static SslContext setSslContext() {
        final SslContext sslCtx;
        if (SSL) {
            try {
				sslCtx = SslContextBuilder.forClient()
				    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
			} catch (SSLException e) {
				e.printStackTrace();
				return null;
			}
        } else {
            sslCtx = null;
        }
        return sslCtx;
    }

}
