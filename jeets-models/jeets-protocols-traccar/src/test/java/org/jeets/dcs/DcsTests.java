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

// TODO set all Tests on default.original.xml for all protocols and ports
public class DcsTests extends CamelTestSupport {

//  compare org.jeets.dcs.DcsSpringBootTests with Context
    private static final Logger LOG = LoggerFactory.getLogger(DcsTests.class);

    @Test
    public void testRunAllServers() throws Exception {
//      load traccar.all.xml
//      from resources !!
//      how to UNinit Context from earlier TraccarSetup.loadConfigured .. ?
    }

    /**
     * The term '-Configured-' refers to the reduced traccar.xml file. It only holds
     * servers with additional testing material like protocols in the repos
     * jeets-data/device.send folder. These tests become part of the JeeTS build,
     * test and integration test runs. The test {@link #testRunAllServers() testrun
     * all servers} is more shallow as it only starts a server and maybe sends a
     * single message.
     */
    @Test
    public void testAllConfiguredServers() throws Exception {
        Map<Integer, Class<?>> protocolClasses = TraccarSetup.loadConfiguredBaseProtocolClasses();

        if (protocolClasses.size() > 0) {
            LOG.info("found " + protocolClasses.size() + " classes configured in configFile");

            for (int port : protocolClasses.keySet()) {
                @SuppressWarnings("unchecked")
                Class<? extends BaseProtocol> clazz = (Class<? extends BaseProtocol>) protocolClasses.get(port);
                String className = clazz.getSimpleName(); // TeltonikaProtocol > teltonika
                String protocolName = className.substring(0, className.length() - 8).toLowerCase();
                
                ServerInitializerFactory pipeline = TraccarSetup.createServerInitializerFactory(clazz);
//              pipeline can be registered with Camel ..
                context.getRegistry().bind(protocolName, pipeline);
//              .. or SpringBoot: @Bean(name = protocolName)
//              beanFactory.registerSingleton(protocolName, pipeline);

//              register netty as jeets-dcs ;)
                String uri = "netty:tcp://" + host + ":" + port 
                        + "?serverInitializerFactory=#" + protocolName + "&sync=false";
//              "&workerPool=#sharedPool&usingExecutorService=false" register in XML,

                context.addRoutes(new TraccarRoute(uri, protocolName)); // id=teltonikaRoute
//              SpringBoot: @Bean(name = protocolName + "Route")
//              beanFactory.registerSingleton(protocolName + "Route", new TraccarRoute(uri, protocolName));

                LOG.info("added server: " + uri);
            }
        
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
            LOG.info("found " + protocolClasses.size() + " classes configured in configFile");

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
        String protocol = "teltonika";

        Class<? extends BaseProtocol> protocolClass = TeltonikaProtocol.class;
        ServerInitializerFactory teltonikaPipeline = 
                TraccarSetup.createServerInitializerFactory(protocolClass);

//      SpringBoot: @Bean(name = "teltonika")
        context.getRegistry().bind(protocol, teltonikaPipeline);
        
        int port = TraccarSetup.getConfiguredProtocolPort(protocol);
//      catch port = 0 ?
//      int port = getPort(protocol + ".port");
        LOG.info(protocol + " port: " + port);
        
        String uri = "netty:tcp://" + host + ":" + port + 
                "?serverInitializerFactory=#" + protocol + "&sync=false";
        context.addRoutes(new TraccarRoute(uri, protocol));
        
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

//      TODO: use teltonika.jdev test file for message content
        String hexMessage = "000f333536333037303432343431303133";
        String hexResponse = sendHexMessage(port, hexMessage); // no dcs output!
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
        LOG.info("Before DcsTests ...");
        TraccarSetup.contextInit(ContextTest.configuredServers);
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
