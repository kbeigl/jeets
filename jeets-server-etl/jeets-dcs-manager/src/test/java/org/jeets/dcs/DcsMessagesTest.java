package org.jeets.dcs;

import io.netty.buffer.ByteBufUtil;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.jeets.protobuf.Jeets;
import org.jeets.protobuf.Jeets.Acknowledge;
import org.jeets.protobuf.Jeets.Device.Builder;
import org.jeets.protocol.util.Samples;
import org.jeets.traccar.TraccarSetup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.traccar.model.Position;

/**
 * Bootstrap the entire container to start complete DCS component, send messages, receive responses
 * and evaluate server input.
 *
 * <p>Two way testing: client sends hex, asserts ack while test case additionally validates and
 * asserts the system entity provided as DCS output.
 *
 * <p>Currently the Tracker additionally sends six jeets protobuffers without explicit testing. This
 * occurs asynchronously and should not effect the tests.
 */
@CamelSpringBootTest
@SpringBootTest(classes = Main.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class DcsMessagesTest {

  private static final Logger LOG = LoggerFactory.getLogger(DcsMessagesTest.class);
  //	TODO test on default.original.xml for all protocols and ports
  //  create exclusive Traccar Server Tests <> Jeets and Netty Decoder Tests

  @Autowired private ProducerTemplate client;
  @Autowired private ConsumerTemplate server;
  private String host = "netty:tcp://localhost:";
  private String threading = "direct:"; // TO TEST: seda
  private String dcsConsumer = threading + "traccar.model";

  /** Test org.traccar.protocol.JeetsDecoder and -Protocol */
  @Test
  public void testTraccarJeetsServer() throws Exception {
    String protocol = "jeets";
    int port = TraccarSetup.getProtocolPort(protocol);

    Builder protoMessage = Samples.createDeviceWithPositionWithOneEvent();
    // TODO: change proto uniqueId to String
    //  String protoDevice = "protoDevice";
    //  protoMessage.setUniqueid(protoDevice);
    LOG.info("sending " + protoMessage);
    Jeets.Acknowledge ack = sendProtoMessage(port, protoMessage);
    LOG.info("received " + ack); // add duration !
    Assertions.assertEquals(123, ack.getDeviceid());

    Position position = server.receiveBody(dcsConsumer, Position.class);
    // Note: different outputs for traccar and jeetsRoute: time, lat-lon precision!?
    // [ebe77164] uniqueId: 395389 protocol: jeets time: 1970-01-01 01:00:00
    // lat: 49,03091    lon: 12,10283 ...
    // jeetsRoute - DCS jeets output: position (   time: Fri Jul 24 11:53:38 CEST 2020
    // lat: 49.03091228 lon: 12.10282818 )
    Assertions.assertEquals(
        position.getAttributes().get("org.jeets.dcs.device.uniqueid"), "395389");
    Assertions.assertEquals(position.getString("org.jeets.dcs.device.uniqueid"), "395389");
    Assertions.assertEquals(position.getInteger("org.jeets.dcs.device.port"), port);
    Assertions.assertEquals(position.getProtocol(), protocol);
  }

  private Jeets.Acknowledge sendProtoMessage(int port, Builder protoMessage) {
    // requires registered ack=JeetsClientProtocol, currently as fixed @Bean
    String nettyParams = "?clientInitializerFactory=#ack&sync=true";
    Jeets.Acknowledge ack =
        (Acknowledge) client.requestBody(host + port + nettyParams, protoMessage);
    return ack;
  }

  @Test
  public void testTeltonikaServer() throws Exception {
    String protocol = "teltonika";
    int port = TraccarSetup.getProtocolPort(protocol);
    // catch port = -1 ?

    // TODO: use teltonika.jdev test file for message content
    String hexMessage = "000f333536333037303432343431303133";
    String hexResponse = sendHexMessage(port, hexMessage);
    Assertions.assertEquals(hexResponse, "01");
    //      no server output!

    hexMessage =
        "000000000000003608010000016b40d8ea30010000000000000000000000000000000105021503010101425e0f01f10000601a014e0000000000000000010000c7cf";
    hexResponse = sendHexMessage(port, hexMessage);
    // assert ACK on client
    Assertions.assertEquals(hexResponse, "00000001");
    // assert position on server's DCS consumer
    Position position = server.receiveBody(dcsConsumer, Position.class);
    Assertions.assertEquals(
        "356307042441013", position.getAttributes().get("org.jeets.dcs.device.uniqueid"));
    Assertions.assertEquals("356307042441013", position.getString("org.jeets.dcs.device.uniqueid"));
    Assertions.assertEquals(port, position.getInteger("org.jeets.dcs.device.port"));
    Assertions.assertEquals(protocol, position.getProtocol());

    hexMessage =
        "000000000000002808010000016b40d9ad80010000000000000000000000000000000103021503010101425e100000010000f22a";
    hexResponse = sendHexMessage(port, hexMessage);
    Assertions.assertEquals("00000001", hexResponse);
    position = server.receiveBody(dcsConsumer, Position.class);
    Assertions.assertEquals(
        "356307042441013", position.getAttributes().get("org.jeets.dcs.device.uniqueid"));
    Assertions.assertEquals("356307042441013", position.getString("org.jeets.dcs.device.uniqueid"));
    Assertions.assertEquals(port, position.getInteger("org.jeets.dcs.device.port"));
    Assertions.assertEquals(protocol, position.getProtocol());
  }

  @Test
  public void testRuptelaServer() throws Exception {
    String protocol = "ruptela";
    int port = TraccarSetup.getProtocolPort(protocol);
    // catch port = 0 ?

    // from ruptela.jdev test file for message content
    String hexMessage =
        "002f0003142b0bae2b9b0100015de029a6000004872bb81e0387440328316a090000090703ad00fb011b0b011e0ff300001d8d";
    String hexResponse = sendHexMessage(port, hexMessage);
    Assertions.assertEquals("0002640113bc", hexResponse);

    Position position = server.receiveBody(dcsConsumer, Position.class);
    Assertions.assertEquals(50.35477d, position.getLatitude(), 0.00001d);
    Assertions.assertEquals(7.59674d, position.getLongitude(), 0.00001d);
    // course: 126.5
    Assertions.assertEquals(
        "866600042245019", position.getAttributes().get("org.jeets.dcs.device.uniqueid"));
    Assertions.assertEquals("866600042245019", position.getString("org.jeets.dcs.device.uniqueid"));
    Assertions.assertEquals("ruptela", protocol);
    Assertions.assertEquals(position.getInteger("org.jeets.dcs.device.port"), port);
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
    String result = (String) client.requestBody(host + port + nettyParams, stringMessage);
    System.out.println("response: " + result);
    Assertions.assertEquals("ACK: " + stringMessage, result);
  }
}
