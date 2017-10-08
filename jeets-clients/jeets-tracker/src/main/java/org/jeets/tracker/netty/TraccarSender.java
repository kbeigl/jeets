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
 * Sender Component to setup Netty and send a binary Traccar message.
 * 
 * @author kbeigl@jeets.org
 */
public class TraccarSender {

    static final boolean SSL = System.getProperty("ssl") != null;

    /**
     * Currently every invocation sets up the complete Netty 'chain' and creates
     * a new connection. The return value of null indicates that the
     * transmission was not successful.
     */
    public static Acknowledge transmitTraccarObject(Object traccarObject, String host, int port) {
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
        } catch (Exception e) {
            System.err.println(e.getMessage());
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
