package org.jeets.dcs;

import java.util.Map;

import org.apache.camel.component.netty.ServerInitializerFactory;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.jeets.traccar.NettyServer;
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

import io.netty.buffer.ByteBufUtil;

/**
 * These JUnit Tests should only validate basic functionality and -messages to
 * detect problems before running the dcs-manager. The dcs-itests should be used
 * to test protocol sequences from files.
 */
public class DcsTests extends CamelTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(DcsTests.class);

	/**
	 * These tests are based on the original traccar- and default.xml files with all
	 * available protocols and ports.
	 */
    @Test
	public void testAllServers() throws Exception {

    	Map<String, NettyServer> servers = TraccarSetup.prepareServers(protocolClasses);
    	LOG.info("created {} servers for {} protocols", servers.size(), protocolClasses.size());

    	int count = 0;
    	if (servers.size() > 0) {
            for (String protocolSpec : servers.keySet()) {
            	NettyServer server = servers.get(protocolSpec);
            	LOG.debug(++count + ". " + protocolSpec);

//              pipeline can be registered with Camel ..
                context.getRegistry().bind(protocolSpec, server.factory);
//              .. or SpringBoot: @Bean(name = protocolSpec)
//              beanFactory.registerSingleton(protocolSpec, server.factory);

                // register netty as jeets-dcs ;)
                // The Consumer Endpoint (from) for each Traccar protocol must be set to
                // sync=false! The Traccar Pipeline and -Decoders are implemented WITH ACK
                // response, i.e. channel.writeAndFlush. Therefore the Camel Endpoint, i.e.
                // NettyConsumer, should NOT return a (additional) response.
                String uri = "netty:" + server.transport + "://" + host + ":" + server.port
                		+ "?serverInitializerFactory=#" + protocolSpec + "&sync=false";
//              		  "&workerPool=#sharedPool&usingExecutorService=false" etc.
//              what about potential server hosts in the configFile?
//              String serverHost = (server.getAddress() == null) ? host : server.getAddress();

                context.addRoutes(new TraccarRoute(uri, protocolSpec)); // id=teltonika-dcsRoute
//              SpringBoot: @Bean(name = protocolSpec + "Route")
//              beanFactory.registerSingleton(protocolSpec + "Route", new TraccarRoute(uri, protocolSpec));
            }
        }

		testTeltonikaMessages();
	}

	@Test
    public void testConfiguredBaseProtocolClasses() throws Exception {
        if (protocolClasses.size() > 0) {

            for (int port : protocolClasses.keySet()) {
                Class<?> clazz = protocolClasses.get(port);
                String className = clazz.getSimpleName(); // TeltonikaProtocol > teltonika
                String protocolName = className.substring(0, className.length() - 8).toLowerCase();
                LOG.debug("protocol: " + protocolName + "\tport#" + port + "\tclass: " + clazz);
            }
        
        } else {
            LOG.warn("No classes found, which are configured in configFile");
//          fail ?
        }
    }
    
    /**
     * Individual server test with explicit *Protocol.class to ensure functionality
     * of a single protocol. The config files are not applied at all.
     */
    @Test
    public void testTeltonikaTcpServer() throws Exception {

        // redundant code from TraccarSetup
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
                
                String uri = "netty:tcp://" + host + ":" + port + 
                        "?serverInitializerFactory=#" + protocolSpec + "&sync=false";
                context.addRoutes(new TraccarRoute(uri, protocolSpec));
			}
    	}
//      now start the actual test
        testTeltonikaMessages();
    }

//  create multi purpose JUnit test 
//  for single messages with thorough tests
//  and for protocols with message counts ..
//  redundant to dcs > DcsSpringBootTests => prototype!

//  collect protocol tests in separate classes for general use
    public void testTeltonikaMessages() throws Exception {
        String protocol = "teltonika";
//      int port = getPort(protocol + ".port");
        int port = TraccarSetup.getProtocolPort(protocol);
//      catch port = 0 ?

        String hexMessage, hexResponse;
//      TODO: use teltonika.jdev test file for message content
        hexMessage = "000f333536333037303432343431303133";
        hexResponse = sendHexMessage(port, hexMessage); // no dcs output!
        Assert.assertEquals("01", hexResponse);

        hexMessage = "000000000000003608010000016b40d8ea30010000000000000000000000000000000105021503010101425e0f01f10000601a014e0000000000000000010000c7cf";
        hexResponse = sendHexMessage(port, hexMessage);
        Assert.assertEquals("00000001", hexResponse);

        Position position = consumer.receiveBody("direct:traccar.model", Position.class);
        Assert.assertEquals("356307042441013", position.getAttributes().get("org.jeets.dcs.device.uniqueid"));
        Assert.assertEquals("356307042441013", position.getString("org.jeets.dcs.device.uniqueid"));
        Assert.assertEquals(port, position.getInteger("org.jeets.dcs.device.port"));
        Assert.assertEquals("teltonika", position.getProtocol());

        hexMessage = "000000000000002808010000016b40d9ad80010000000000000000000000000000000103021503010101425e100000010000f22a";
        hexResponse = sendHexMessage(port, hexMessage);
        Assert.assertEquals("00000001", hexResponse);

        position = consumer.receiveBody("direct:traccar.model", Position.class);
        Assert.assertEquals("356307042441013", position.getAttributes().get("org.jeets.dcs.device.uniqueid"));
        Assert.assertEquals("356307042441013", position.getString("org.jeets.dcs.device.uniqueid"));
        Assert.assertEquals(port, position.getInteger("org.jeets.dcs.device.port"));
        Assert.assertEquals("teltonika", position.getProtocol());
    }

//  compare jeets-device code and jeets-dcs Test - redundant code!

    private String sendHexMessage(int port, String hexMessage) {
        byte[] byteMessage = ByteBufUtil.decodeHexDump(hexMessage);
        String nettyParams = "?useByteBuf=true&allowDefaultCodec=false&producerPoolEnabled=false";
        byte[] response = template.requestBody("netty:tcp://localhost:" + port + nettyParams, byteMessage, byte[].class);
        return ByteBufUtil.hexDump(response);
    }

    /**
     * camel-netty and/or spring are/is tedious about localhost, which doesn't
     * accept external access (in ubuntu). On the remote system 0.0.0.0 should be
     * used instead of 127.0.0.1.
     */
    private String host = "0.0.0.0";

    private static Map<Integer, Class<?>> protocolClasses;
    
    /**
     * Hard coded setup with relative path and file must exist in the project to
     * allow testing the repositories integrity!
     * @throws Exception 
     */
    @BeforeClass
    public static void setup() throws Exception {
        TraccarSetup.contextInit(ContextTest.configuredServers);
        protocolClasses = TraccarSetup.loadProtocolClasses();
    }

    /*
     * SHOULD BE HARD CODED FOR TRACCAR
     * The from endpoint for each protocol must be set to false!
     * <p>
     * The Traccar Pipeline and -Decoders are implemented WITH ACK response, i.e.
     * channel.writeAndFlush. Therefore the Camel endpoint, i.e. NettyConsumer,
     * should NOT return any (additional) response. This behavior should be observed ..
     * <br>
     * Note that this boolean variable is attached to the URI as String 'true' /
     * 'false'. Maybe apply String for type safety.
     */
//  private boolean camelNettySync = false;

}
