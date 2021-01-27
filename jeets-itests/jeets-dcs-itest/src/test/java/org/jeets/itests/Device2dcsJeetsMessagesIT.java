package org.jeets.itests;

import java.util.Date;
import java.util.List;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.jeets.dcs.Main;
import org.jeets.protobuf.Jeets; // Acknowledge, Device, Position
import org.jeets.protocol.util.Samples;
import org.jeets.traccar.TraccarSetup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

// run single IT: mvn verify -Dit.test=Device2dcsJeetsMessagesIT -Pitests

/**
 * Send single (jeets protobuffer) messages and assert each message, if needed. Camels
 * ProducerTemplate is used to send messages instead of an external tracker.
 */
@CamelSpringBootTest
@SpringBootTest(classes = Main.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@MockEndpoints
public class Device2dcsJeetsMessagesIT {
  // compare dcs DcsSpringBootTests
  // replaces tracker project
  // to be moved to Device.send(protobuffer)

  //	test gps103 message
  //  int port = 5001;            //  traccar default gps103.port
  //  String messageString =      //  valid gps103 sample
  //          "imei:359587010124999,help me,1710021201,," +
  //          "F,120100.000,A,4900.0000,N,1200.0000,E,0.00,;";

  // TODO: validateMessages and add more Assertions

  @Autowired private ProducerTemplate client;

  @EndpointInject(uri = "mock:result")
  private MockEndpoint mockDcs;

  @Test
  public void testJeetsMessages() throws Exception {
    int positionCount = sendJeetsMessages();
    mockDcs.expectedMessageCount(positionCount);
    mockDcs.assertIsSatisfied();
    // empty method as of now
    validateMessages(mockDcs.getExchanges());
    mockDcs.reset(); // for next test
  }

  //  @Test
  public void testJeetsMessagesAgain() throws Exception {
    mockDcs.expectedMessageCount(sendJeetsMessages());
    mockDcs.assertIsSatisfied();
    mockDcs.reset();
  }

  /** Test creation, send, receive and unmarshal ack of Jeets Protobuffer Devices. */
  //  TODO create HEX file from dcs-traccar log and setup additional jeets file test
  private int sendJeetsMessages() throws Exception {
    String protocol = "jeets";
    int port = TraccarSetup.getProtocolPort(protocol);

    // msg#1 - 1 Position
    int positionCount = 1;
    // use any of the prepared Samples here
    Jeets.Device.Builder protoDeviceBuilder = Samples.createDeviceWithPositionWithOneEvent();
    // System.out.println("sending " + protoMessage);
    Jeets.Acknowledge ack = sendProtoDevice(port, protoDeviceBuilder);
    // System.out.println("received " + ack);
    // Assert.assertEquals(ack.getDeviceid(), protoDevice);
    Assertions.assertEquals(123, ack.getDeviceid());

    List<Jeets.Position.Builder> posBuilderList = Samples.createSampleTrack();
    System.out.println("created list with " + posBuilderList.size() + " positions.");
    // msg#2 - 1 Position
    positionCount++;
    posBuilderList.get(0).setFixtime(new Date().getTime());
    protoDeviceBuilder = Jeets.Device.newBuilder().setUniqueid("route");
    protoDeviceBuilder.addPosition(posBuilderList.get(0));
    // Jeets.Device protoDevice = devBuilder.build();
    // use: java.util.UUID xTraceID = java.util.UUID.randomUUID();
    System.out.println("transmitTraccarDevice: " + protoDeviceBuilder);
    // ack = TraccarSender.transmitTraccarObject(protoDevice, host, port);
    ack = sendProtoDevice(port, protoDeviceBuilder);
    System.out.println("received Acknowledge: " + ack + " at " + new Date());

    // msg#3 - 2 Positions
    positionCount += 2;
    posBuilderList.get(1).setFixtime(new Date().getTime());
    posBuilderList.get(2).setFixtime(new Date().getTime());
    protoDeviceBuilder = Jeets.Device.newBuilder().setUniqueid("route");
    protoDeviceBuilder.addPosition(posBuilderList.get(1)).addPosition(posBuilderList.get(2));
    System.out.println("transmitTraccarDevice: " + protoDeviceBuilder);
    // ack = TraccarSender.transmitTraccarObject(protoDevice, host, port);
    ack = sendProtoDevice(port, protoDeviceBuilder);
    System.out.println("received Acknowledge: " + ack + " at " + new Date());

    // msg#4 - 3 Positions
    positionCount += 3;
    posBuilderList.get(3).setFixtime(new Date().getTime());
    posBuilderList.get(4).setFixtime(new Date().getTime());
    posBuilderList.get(5).setFixtime(new Date().getTime());
    protoDeviceBuilder = Jeets.Device.newBuilder().setUniqueid("route");
    protoDeviceBuilder
        .addPosition(posBuilderList.get(3))
        .addPosition(posBuilderList.get(4))
        .addPosition(posBuilderList.get(5));
    System.out.println("transmitTraccarDevice: " + protoDeviceBuilder);
    // ack = TraccarSender.transmitTraccarObject(protoDevice, host, port);
    ack = sendProtoDevice(port, protoDeviceBuilder);
    System.out.println("received Acknowledge: " + ack + " at " + new Date());

    return positionCount;
  }

  private void validateMessages(List<Exchange> exchanges) {
    /*
            for (int nr = 0; nr < exchanges.size(); nr++) {
    //          assert instanceOf Device  TODO Position
                Device dev = (Device) exchanges.get(nr).getIn().getBody();
    //          LOG.info("  Device#{}  sent:{} received:{}", nr, dev.getLastupdate(), new Date());
    //          inside IT message should be sent in the last ten minutes - keep it safe
                assertEquals(0, (new Date().getTime() - dev.getLastupdate().getTime()), 10*60*1000);
                List<Position> devPositions = dev.getPositions();
                Date pos0fix = devPositions.get(0).getFixtime();
                for (int pos = 1; pos < devPositions.size(); pos++) {
                    LOG.info("Position#{} fixed:{}   server:{}", pos, devPositions.get(pos).getFixtime(), devPositions.get(pos).getServertime());
                    assertTrue("fixtimes are not in chronological order!", pos0fix.before(devPositions.get(pos).getFixtime()));
                    pos0fix = devPositions.get(pos).getFixtime();
                }
            }
     */
  }

  private String host = "netty:tcp://localhost:";

  // client producer to replace tracker project!
  private Jeets.Acknowledge sendProtoDevice(int port, Jeets.Device.Builder protoDevice) {
    // requires registered ack=JeetsClientProtocol, currently as fixed @Bean in dcs-mgr
    String nettyParams = "?clientInitializerFactory=#ack&sync=true";
    Jeets.Acknowledge ack =
        (Jeets.Acknowledge) client.requestBody(host + port + nettyParams, protoDevice);
    return ack;
  }
}
