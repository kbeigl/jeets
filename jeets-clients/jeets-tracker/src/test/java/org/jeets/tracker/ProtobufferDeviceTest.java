package org.jeets.tracker;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.jeets.protobuf.Jeets;
import org.jeets.protocol.util.Samples;
import org.jeets.tracker.netty.TraccarAckInitializer;
import org.jeets.tracker.netty.TraccarMessageHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class ProtobufferDeviceTest {
	
	private static final Logger log = LoggerFactory.getLogger(ProtobufferDeviceTest.class);

    static final String PORT = System.getProperty("port", "test_port");
    private static final int TIMEOUT_MILLIS = 1000;
    // Address to bind on / connect to.
    private static final LocalAddress SERVER_ADDRESS = new LocalAddress(PORT);
    private EventLoopGroup group;
    private ServerBootstrap server;
    private Bootstrap client;

    @Test
    public void testProtobufferDevice() {
        Jeets.Device.Builder deviceBuilder = Samples.createDeviceWithPositionWithOneEvent();
        assertEquals(org.jeets.model.traccar.util.Samples.uniqueId, deviceBuilder.getUniqueid());
        
        String testId = "testId";
        deviceBuilder.setUniqueid(testId);
        Jeets.Device deviceOrm = deviceBuilder.build();

        assertEquals(testId, deviceOrm.getUniqueid());
        assertTrue(deviceOrm.getPositionCount()==1);
        assertTrue(deviceOrm.getPosition(0).getEventCount()==1);

        log.info("transmit Device Proto:\n{}", deviceBuilder.toString());
        Channel clientChannel = null;   // , serverChannel = null;
        try {
            // Start the server.
            server.bind(SERVER_ADDRESS).sync();
            // Start the client.
            clientChannel = client.connect(SERVER_ADDRESS).sync().channel();
//          Tracker.transmitTraccarDevice(deviceOrm, host, port);
            TraccarMessageHandler handler = clientChannel.pipeline().get(TraccarMessageHandler.class);
//          Traccar.Acknowledge ack = handler.sendTraccarObject(deviceBuilder);
            Jeets.Acknowledge ack = handler.sendTraccarObject(deviceOrm);
//          assertEquals("..", ack.toString());
            
            log.info("Client received\n{}", ack.toString());
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            closeChannel(clientChannel);
//          closeChannel(serverChannel);
        }
    }

    @Before
    public void setUp() {
        group = new DefaultEventLoopGroup(1);
        server = new ServerBootstrap()
                .group(group)
                .channel(LocalServerChannel.class)
                .handler(new ChannelInitializer<LocalServerChannel>() {
                    @Override
                    public void initChannel(LocalServerChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                    }
                })
                .childHandler(new ChannelInitializer<LocalChannel>() {
                    @Override
                    public void initChannel(LocalChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                new LoggingHandler(LogLevel.INFO),
                                new ProtobufVarint32FrameDecoder(),
                                new ProtobufDecoder(Jeets.Device.getDefaultInstance()),
                                new ProtobufVarint32LengthFieldPrepender(),
                                new ProtobufEncoder(),
                                new TraccarServerHandler());
                    }
                });
        client = new Bootstrap()
                .group(group)
                .channel(LocalChannel.class)
                .handler(new TraccarAckInitializer<LocalChannel>(null, null, 0));
    }

    @After
    public void tearDown() {
        group.shutdownGracefully(0, TIMEOUT_MILLIS, TimeUnit.MILLISECONDS).syncUninterruptibly();
    }

    private static void closeChannel(Channel c) {
        if (c != null) {
            c.close().syncUninterruptibly();
        }
    }

}
