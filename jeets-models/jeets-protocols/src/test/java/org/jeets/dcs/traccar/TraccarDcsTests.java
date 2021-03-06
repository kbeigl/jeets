package org.jeets.dcs.traccar;

import io.netty.buffer.ByteBufUtil;
import org.apache.camel.component.netty.ServerInitializerFactory;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.jeets.traccar.TraccarRoute;
import org.jeets.traccar.TraccarSetup;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.traccar.BaseProtocol;
import org.traccar.TrackerServer;
import org.traccar.model.Position;
import org.traccar.protocol.TeltonikaProtocol;

public class TraccarDcsTests extends CamelTestSupport {

  //  compare org.jeets.dcs.DcsSpringBootTests with Context
  //  redundant tests in jeets-protocols-traccar > unify
  private static final Logger LOG = LoggerFactory.getLogger(TraccarDcsTests.class);

  /** Example of how to setup a single BaseProtocol server */
  @Test
  public void testTeltonikaTcpServer() throws Exception {

    Class<? extends BaseProtocol> protocolClass = TeltonikaProtocol.class;
    String protocolName = "teltonika";
    BaseProtocol protocolInstance = TraccarSetup.instantiateProtocol(protocolClass);

    for (TrackerServer server : protocolInstance.getServerList()) {
      if (!server.isDatagram()) {
        String transport = "tcp";
        String protocolSpec = protocolName + "-" + transport; // append transport
        ServerInitializerFactory factory = server.getServerInitializerFactory();

        //              SpringBoot: @Bean(name = "teltonika")
        context.getRegistry().bind(protocolSpec, factory);

        int port = TraccarSetup.getProtocolPort(protocolName);
        //              catch port = 0 ?
        //              int port = getPort(protocol + ".port");
        LOG.info(protocolName + " port: " + port);

        String uri =
            "netty:tcp://"
                + host
                + ":"
                + port
                + "?serverInitializerFactory=#"
                + protocolSpec
                + "&sync=false";
        context.addRoutes(new TraccarRoute(uri, protocolSpec));
      }
    }
    //      now start the actual test
    testTeltonikaMessages();
  }

  //  redundant to dcs > DcsSpringBootTests => prototype!
  public void testTeltonikaMessages() throws Exception {
    String protocol = "teltonika";
    //      int port = getPort(protocol + ".port");
    int port = TraccarSetup.getProtocolPort(protocol);
    //      catch port = 0 ?

    //      TODO: use teltonika.jdev test file for message content
    String hexMessage = "000f333536333037303432343431303133";
    String hexResponse = sendHexMessage(port, hexMessage);
    Assert.assertEquals("01", hexResponse);
    //      no server output!

    hexMessage =
        "000000000000003608010000016b40d8ea30010000000000000000000000000000000105021503010101425e0f01f10000601a014e0000000000000000010000c7cf";
    hexResponse = sendHexMessage(port, hexMessage);
    Assert.assertEquals("00000001", hexResponse);
    Position position = consumer.receiveBody("direct:traccar.model", Position.class);
    Assert.assertEquals(
        "356307042441013", position.getAttributes().get("org.jeets.dcs.device.uniqueid"));
    Assert.assertEquals("356307042441013", position.getString("org.jeets.dcs.device.uniqueid"));
    Assert.assertEquals(port, position.getInteger("org.jeets.dcs.device.port"));
    Assert.assertEquals("teltonika", position.getProtocol());

    hexMessage =
        "000000000000002808010000016b40d9ad80010000000000000000000000000000000103021503010101425e100000010000f22a";
    hexResponse = sendHexMessage(port, hexMessage);
    Assert.assertEquals("00000001", hexResponse);
    position = consumer.receiveBody("direct:traccar.model", Position.class);
    Assert.assertEquals(
        "356307042441013", position.getAttributes().get("org.jeets.dcs.device.uniqueid"));
    Assert.assertEquals("356307042441013", position.getString("org.jeets.dcs.device.uniqueid"));
    Assert.assertEquals(port, position.getInteger("org.jeets.dcs.device.port"));
    Assert.assertEquals("teltonika", position.getProtocol());
  }

  //  compare jeets-device code and jeets-dcs Test - redundant code!
  private String sendHexMessage(int port, String hexMessage) {
    byte[] byteMessage = ByteBufUtil.decodeHexDump(hexMessage);
    String nettyParams = "?useByteBuf=true&allowDefaultCodec=false&producerPoolEnabled=false";
    byte[] response =
        template.requestBody(
            "netty:tcp://localhost:" + port + nettyParams, byteMessage, byte[].class);
    return ByteBufUtil.hexDump(response);
  }

  //  Traccar Context must be initialized ..

  //  TODO: apply availablePortFinder instead of config file ?
  //    private int getPort(String protocolPort) {
  //        Assert.assertTrue(protocolPort + " is not defined in config file!",
  //               Context.getConfig().hasKey(protocolPort));
  //        return Context.getConfig().getInteger(protocolPort);
  //    }

  /**
   * The from endpoint for each protocol must be set to false!
   *
   * <p>The Traccar Pipeline and -Decoders are implemented WITH ACK response, i.e.
   * channel.writeAndFlush. Therefore the Camel endpoint, i.e. NettyConsumer, should NOT return a
   * (addtional) response. This behavior should be observed .. <br>
   * Note that this boolean variable is attached to the URI as String 'true' / 'false'. Maybe apply
   * String for type safety.
   */
  private boolean camelNettySync = false;
  /**
   * camel-netty and/or spring are/is tedious about localhost, which doesn't accept external access
   * (in ubuntu). On the remote system 0.0.0.0 should be used instead of 127.0.0.1.
   */
  private String host = "0.0.0.0";

  @BeforeClass
  public static void setup() {
    LOG.info("startup - Context.init ...");
    TraccarSetup.contextInit(".\\setup\\traccar.xml");
  }
}
