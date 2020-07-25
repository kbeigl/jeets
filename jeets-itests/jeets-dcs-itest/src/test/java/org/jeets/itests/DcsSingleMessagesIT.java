package org.jeets.itests;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ProducerTemplate;
import org.jeets.dcs.Main;
import org.jeets.protobuf.Jeets;
import org.jeets.protobuf.Jeets.Acknowledge;
import org.jeets.protobuf.Jeets.Device.Builder;
import org.jeets.protocol.util.Samples;
import org.jeets.traccar.TraccarSetup;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.traccar.model.Position;
import io.netty.buffer.ByteBufUtil;

// run this test from command line: mvn -Dit.test=DcsSpringBootIT verify -Pitests

/**
 * Bootstrap the entire container to start complete DCS component, send
 * messages, receive responses and evaluate server input.
 * <p>
 * Two way testing: client sends hex, asserts ack while test case additionally
 * validates and asserts the system entity provided as DCS output.
 * <p>
 * Currently the Tracker additionally sends six jeets protobuffers without
 * explicit testing. This occurs asynchronously and should not effect the tests.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Main.class)
public class DcsSingleMessagesIT {
    
    /*
     * Note that these tests are sending and asserting explicit single messages
     * without the need of external processes.
     * 1. Therefore this test should run inside mvn install without -IT > doesn't work ?
     * on the other hand:
     * 2. This test is booting the DCS and keeps it alive for following Device2dcsIT
     *    which makes use of device.send - only makes sense if DCS is running (?)
     * a. while the Tracker protobuffer messages keep coming in
     *    currently without test, generally also test chronological order !
     * 3. setup tests for protocol files sent by Device > long integration tests 
     *    (i.e. move file > test nr or msgs ..)
     */

//  TODO harmonize / adjust with jeets-dcs DcsSpringBootTests
//  create exclusive Traccar Server Tests <> Jeets and Netty Decoder Tests
    
//  TODO implement testJeetsServer with Camel ACK, implement properties
    
    private static final Logger LOG = LoggerFactory.getLogger(DcsSingleMessagesIT.class);
    @Autowired
    private ProducerTemplate client;
    @Autowired
    private ConsumerTemplate server;
    private String host = "netty:tcp://localhost:";
    private String threading = "direct:"; // TO TEST: seda
    private String dcsConsumer = threading + "traccar.model";
    
    /**
     * Test org.jeets.protocol.JeetsDecoder and -Protocol
     * 
     * @throws Exception
     */
    @Test
    public void testJeetsServer() throws Exception {

//      currently this explicit jeets server is not launched automatically
//      implement with Camel ACK mechanism
//      implement config mechanism with props file
//      see template DcsJeetsProtocolIT for a start (with Camel LoadTypeConverters ..)

    }

    /**
     * Test org.traccar.protocol.JeetsDecoder and -Protocol
     */
    @Test
    public void testTraccarJeetsServer() throws Exception {
        String protocol = "jeets";
        int port = TraccarSetup.getConfiguredProtocolPort(protocol);

        Builder protoMessage = Samples.createDeviceWithPositionWithOneEvent();
//      TODO: change proto uniqueId to String
//      String protoDevice = "protoDevice";
//      protoMessage.setUniqueid(protoDevice);
        LOG.info("sending " + protoMessage);
        Jeets.Acknowledge ack = sendProtoMessage(port, protoMessage);
        LOG.info("received " + ack); // add duration !
        Assert.assertEquals(ack.getDeviceid(), 123);

        Position position = server.receiveBody(dcsConsumer, Position.class);
//      Note: different outputs for traccar and jeetsRoute: time, lat-lon precision!?
//      [ebe77164] uniqueId: 395389 protocol: jeets time: 1970-01-01 01:00:00           lat: 49,03091    lon: 12,10283 ...
//      jeetsRoute - DCS jeets output: position (   time: Fri Jul 24 11:53:38 CEST 2020 lat: 49.03091228 lon: 12.10282818 )
        Assert.assertEquals("395389", position.getAttributes().get("org.jeets.dcs.device.uniqueid"));
        Assert.assertEquals("395389", position.getString("org.jeets.dcs.device.uniqueid"));
        Assert.assertEquals(port, position.getInteger("org.jeets.dcs.device.port"));
        Assert.assertEquals(protocol, position.getProtocol());
    }

    private Jeets.Acknowledge sendProtoMessage(int port, Builder protoMessage) {
//      requires registered ack=JeetsClientProtocol, currently as fixed @Bean
        String nettyParams = "?clientInitializerFactory=#ack&sync=true";
        Jeets.Acknowledge ack = (Acknowledge) 
                client.requestBody(host + port + nettyParams, protoMessage);
        return ack;
    }
    
    @Test
    public void testTeltonikaServer() throws Exception {
        String protocol = "teltonika";
        int port = TraccarSetup.getConfiguredProtocolPort(protocol);
//      catch port = -1 ?

//      TODO: use teltonika.jdev test file for message content
        String hexMessage = "000f333536333037303432343431303133";
        String hexResponse = sendHexMessage(port, hexMessage);
        Assert.assertEquals("01", hexResponse);
//      no server output!

        hexMessage = "000000000000003608010000016b40d8ea30010000000000000000000000000000000105021503010101425e0f01f10000601a014e0000000000000000010000c7cf";
        hexResponse = sendHexMessage(port, hexMessage);
//      assert ACK on client
        Assert.assertEquals("00000001", hexResponse);
//      assert position on server's DCS consumer
        Position position = server.receiveBody(dcsConsumer, Position.class);
        Assert.assertEquals("356307042441013", position.getAttributes().get("org.jeets.dcs.device.uniqueid"));
        Assert.assertEquals("356307042441013", position.getString("org.jeets.dcs.device.uniqueid"));
        Assert.assertEquals(port, position.getInteger("org.jeets.dcs.device.port"));
        Assert.assertEquals(protocol, position.getProtocol());

        hexMessage = "000000000000002808010000016b40d9ad80010000000000000000000000000000000103021503010101425e100000010000f22a";
        hexResponse = sendHexMessage(port, hexMessage);
        Assert.assertEquals("00000001", hexResponse);
        position = server.receiveBody(dcsConsumer, Position.class);
        Assert.assertEquals("356307042441013", position.getAttributes().get("org.jeets.dcs.device.uniqueid"));
        Assert.assertEquals("356307042441013", position.getString("org.jeets.dcs.device.uniqueid"));
        Assert.assertEquals(port, position.getInteger("org.jeets.dcs.device.port"));
        Assert.assertEquals(protocol, position.getProtocol());
        
    }

    @Test
    public void testRuptelaServer() throws Exception {
        String protocol = "ruptela";
        int port = TraccarSetup.getConfiguredProtocolPort(protocol);
//      catch port = 0 ?

//      from ruptela.jdev test file for message content
        String hexMessage = "002f0003142b0bae2b9b0100015de029a6000004872bb81e0387440328316a090000090703ad00fb011b0b011e0ff300001d8d";
        String hexResponse = sendHexMessage(port, hexMessage);
        Assert.assertEquals("0002640113bc", hexResponse);
        
        Position position = server.receiveBody(dcsConsumer, Position.class);
        Assert.assertEquals(50.35477d, position.getLatitude() , 0.00001d);
        Assert.assertEquals( 7.59674d, position.getLongitude(), 0.00001d);
//      course: 126.5
        Assert.assertEquals("866600042245019", position.getAttributes().get("org.jeets.dcs.device.uniqueid"));
        Assert.assertEquals("866600042245019", position.getString("org.jeets.dcs.device.uniqueid"));
        Assert.assertEquals(protocol, "ruptela");
        Assert.assertEquals(port, position.getInteger("org.jeets.dcs.device.port"));
    }

//  compare jeets-device and jeets-protocols redundant code
    private String sendHexMessage(int port, String hexMessage) {
        byte[] byteMessage = ByteBufUtil.decodeHexDump(hexMessage);
        String nettyParams = "?useByteBuf=true&allowDefaultCodec=false&producerPoolEnabled=false";
        byte[] response = client.requestBody(host + port + nettyParams, byteMessage, byte[].class);
        return ByteBufUtil.hexDump(response);
    }

    @Test
    public void testStringEndpoint() throws Exception {
        String stringMessage = "StringMessage";
        String nettyParams = "?useByteBuf=true&decoders=#stringDecoder";
        int port = 7000; // hard coded
        System.out.println("request : " + stringMessage);
        String result = (String) client.
                requestBody(host + port + nettyParams, stringMessage);
        System.out.println("response: " + result);
        Assert.assertEquals("ACK: " + stringMessage, result);
    }
    
}
