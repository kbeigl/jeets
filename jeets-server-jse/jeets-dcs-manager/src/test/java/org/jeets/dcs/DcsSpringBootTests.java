package org.jeets.dcs;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ProducerTemplate;
import org.jeets.protobuf.Jeets;
import org.jeets.protobuf.Jeets.Acknowledge;
import org.jeets.protobuf.Jeets.Device.Builder;
import org.jeets.protocol.util.Samples;
import org.jeets.traccar.TraccarSetup;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.traccar.model.Position;
import io.netty.buffer.ByteBufUtil;

/**
 * Bootstrap the entire container to start complete DCS component, send
 * messages, receive responses and evaluate server input.
 * <p>
 * Two way testing: client sends hex, asserts ack while test case additionally
 * validates and asserts the system entity provided as DCS output.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest // (classes = Main.class)
public class DcsSpringBootTests {
    
//  create exclusive Traccar Server Tests <> Jeets and Netty Decoder Tests

    @Autowired
    private ProducerTemplate client;
    @Autowired
    private ConsumerTemplate server;
    private String host = "netty:tcp://localhost:";
    
    /**
     * Test org.jeets.protocol.JeetsDecoder and -Protocol
     * 
     * @throws Exception
     */
    @Test
    public void testJeetsServer() throws Exception {
        
//      no config mechanism yet, use props file
        
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
        System.out.println("sending " + protoMessage);
        Jeets.Acknowledge ack = sendProtoMessage(port, protoMessage);
        System.out.println("received " + ack);
//      Assert.assertEquals(ack.getDeviceid(), protoDevice);
        Assert.assertEquals(ack.getDeviceid(), 123);

//      This happens every other run (in Eclipse?) ? check DCS Consumer !
// ???  [ShutdownTask] o.a.c.i.engine.DefaultShutdownStrategy: 
//      Waiting as there are still 1 inflight and pending exchanges to complete, timeout in 45 seconds. 
//      Inflights per route: [jeetsRoute = 1]
//      [ServerTCPWorker] org.traccar.MainEventHandler: [3e4ef5b6] disconnected AFTER 30s (locate timeout config)
        
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
        Assert.assertEquals("00000001", hexResponse);
        Position position = server.receiveBody("direct:traccar.model", Position.class);
        Assert.assertEquals("356307042441013", position.getAttributes().get("org.jeets.dcs.device.uniqueid"));
        Assert.assertEquals("356307042441013", position.getString("org.jeets.dcs.device.uniqueid"));
        Assert.assertEquals(port, position.getInteger("org.jeets.dcs.device.port"));
        Assert.assertEquals("teltonika", position.getProtocol());

        hexMessage = "000000000000002808010000016b40d9ad80010000000000000000000000000000000103021503010101425e100000010000f22a";
        hexResponse = sendHexMessage(port, hexMessage);
        Assert.assertEquals("00000001", hexResponse);
        position = server.receiveBody("direct:traccar.model", Position.class);
        Assert.assertEquals("356307042441013", position.getAttributes().get("org.jeets.dcs.device.uniqueid"));
        Assert.assertEquals("356307042441013", position.getString("org.jeets.dcs.device.uniqueid"));
        Assert.assertEquals(port, position.getInteger("org.jeets.dcs.device.port"));
        Assert.assertEquals("teltonika", position.getProtocol());
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
        
        Position position = server.receiveBody("direct:traccar.model", Position.class);
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
