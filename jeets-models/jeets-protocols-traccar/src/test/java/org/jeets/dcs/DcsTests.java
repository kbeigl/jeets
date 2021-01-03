package org.jeets.dcs;

import java.util.Map;

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
import org.traccar.model.Position;
import org.traccar.protocol.TeltonikaProtocol;

import io.netty.buffer.ByteBufUtil;

/**
 * These JUnit Tests should only validate basic functionality and -messages to
 * detect problems before running the dcs-manager. Its
 * org.jeets.dcs.traccar.ServerManager is designed after the original
 * org.traccar.ServerManager The dcs-itests should be used to test protocol
 * sequences from files.
 */
public class DcsTests extends CamelTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(DcsTests.class);

    @Test
    public void testAllConfiguredServers() throws Exception {
        Map<Integer, Class<?>> protocolClasses = TraccarSetup.loadConfiguredBaseProtocolClasses();
        int servers = 0;

        if (protocolClasses.size() > 0) {
            for (int port : protocolClasses.keySet()) {

                @SuppressWarnings("unchecked")
                Class<? extends BaseProtocol> clazz = (Class<? extends BaseProtocol>) protocolClasses.get(port);
                String className = clazz.getSimpleName(); // TeltonikaProtocol > teltonika
                String protocolName = className.substring(0, className.length() - 8).toLowerCase();
                
                Map<String, ServerInitializerFactory> serverInitializerFactories = 
                		TraccarSetup.createServerInitializerFactories(clazz);

                //            <transport, factory>
                for (Map.Entry<String, ServerInitializerFactory> factory : serverInitializerFactories.entrySet()) {

                	// TODO handle java.net.BindException: Address already in use: bind
                	//      and provide free port with port finder !?
                    // netstat -ant > PID > Dienste: Plattformdienst für verbundene Geräte
                	if (port == 5040) // move up and change inner loop break to outer continue
                		break;

                    String protocolSpec = protocolName + "-" + factory.getKey(); // append transport
                    System.out.println(protocolSpec + ": " + factory.getValue());
//                  pipeline can be registered with Camel ..
                    context.getRegistry().bind(protocolSpec, factory.getValue());
//                  .. or SpringBoot: @Bean(name = protocolName)
//                  beanFactory.registerSingleton(protocolName, pipeline);

//                  register netty as jeets-dcs ;)
                    String uri = "netty:" + factory.getKey() + "://" + host + ":" + port
                    		+ "?serverInitializerFactory=#" + protocolSpec + "&sync=false";
//                  		  "&workerPool=#sharedPool&usingExecutorService=false" etc.

                    context.addRoutes(new TraccarRoute(uri, protocolSpec)); // id=teltonikaRoute
//                  SpringBoot: @Bean(name = protocolName + "Route")
//                  beanFactory.registerSingleton(protocolName + "Route", new TraccarRoute(uri, protocolName));

                    servers++; // separate udp and tcp ?
                    LOG.info("added server: " + uri);
                }
            }
            LOG.info("added {} servers", servers);
            
//          now start the individual server tests
            testTeltonikaMessages();

        } else {
            LOG.warn("No classes found, which are configured in configFile");
        }
    }

    @Test
    public void testConfiguredBaseProtocolClasses() throws Exception {
        Map<Integer, Class<?>> protocolClasses = TraccarSetup.loadConfiguredBaseProtocolClasses();

        if (protocolClasses.size() > 0) {

            for (int port : protocolClasses.keySet()) {
                Class<?> clazz = protocolClasses.get(port);
                String className = clazz.getSimpleName(); // TeltonikaProtocol > teltonika
                String protocolName = className.substring(0, className.length() - 8).toLowerCase();
                LOG.info("protocol: " + protocolName + "\tport#" + port + "\tclass: " + clazz);
            }
        
        } else {
            LOG.warn("No classes found, which are configured in configFile");
//          fail ?
        }
    }
    
    /**
     * Individual server test with explicit *Protocol.class to ensure functionality
     * of a single protocol.
     */
    @Test
    public void testTeltonikaServer() throws Exception {
        String protocolName = "teltonika";

        Class<? extends BaseProtocol> protocolClass = TeltonikaProtocol.class;
        Map<String, ServerInitializerFactory> serverInitializerFactories = 
        		TraccarSetup.createServerInitializerFactories(protocolClass);

        for (Map.Entry<String, ServerInitializerFactory> factory : serverInitializerFactories.entrySet()) {
        	if (factory.getKey().equals("tcp")) {
        		
                String protocolSpec = protocolName + "-" + factory.getKey(); // append transport

//              SpringBoot: @Bean(name = "teltonika")
                context.getRegistry().bind(protocolSpec, factory.getValue());
                
                int port = TraccarSetup.getConfiguredProtocolPort(protocolName);
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

    public void testTeltonikaMessages() throws Exception {
        String protocol = "teltonika";
//      int port = getPort(protocol + ".port");
        int port = TraccarSetup.getConfiguredProtocolPort(protocol);
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

    /**
     * Hard coded setup with relative path and file must exist in the project to
     * allow testing the repositories integrity!
     */
    @BeforeClass
    public static void setup() {
        TraccarSetup.contextInit(ContextTest.configuredServers);
//      add member for all tests - and remove from tests
//      Map<Integer, Class<?>> protocolClasses = TraccarSetup.loadConfiguredBaseProtocolClasses();
    }

    /*
     * SHOULD BE HARD CODED FOR TRACCAR
     * The from endpoint for each protocol must be set to false!
     * <p>
     * The Traccar Pipeline and -Decoders are implemented WITH ACK response, i.e.
     * channel.writeAndFlush. Therefore the Camel endpoint, i.e. NettyConsumer,
     * should NOT return a (additional) response. This behavior should be observed ..
     * <br>
     * Note that this boolean variable is attached to the URI as String 'true' /
     * 'false'. Maybe apply String for type safety.
    private boolean camelNettySync = false;
     */

}
