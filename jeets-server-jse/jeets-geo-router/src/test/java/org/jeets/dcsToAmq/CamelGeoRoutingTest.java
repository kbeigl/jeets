package org.jeets.dcsToAmq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.jeets.dcsToAmq.steps.DeviceProtoExtractor;
import org.jeets.georouter.DcsRoutes;
import org.jeets.georouter.Main;
import org.jeets.model.traccar.jpa.Device;
import org.jeets.protocol.Traccar;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kbeigl@jeets.org
 */
public class CamelGeoRoutingTest extends CamelTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(CamelGeoRoutingTest.class);

    @Test
    public void testDeviceToTileRouter() throws Exception {
        
//        how to send 
//        org.jeets.protocol.util.Samples.createDeviceWithPositionWithOneEvent()
//        to MQ device.in ?
        
    }
        
    @Test
    public void testDeviceFromNettyToAmqToMock() throws Exception {
            
        LOG.info("create and add DcsRoute ...");
//      from "netty4:tcp://localhost:{port}?serverInitializerFactory=#device&sync=true"
//      .to "activemq:queue:device.in" :
        context.addRoutes(new DcsRoutes() );
//      context.addRoutes(new GeoRouteS() );  =======================
        context.addRoutes(new RouteBuilder() {
            public void configure() throws Exception {
                from("activemq:queue:device.in?connectionFactory=#activeMqConnectionFactory")
//              .process(new GeoRouteR())
//                .choice()
//                .when(body().matches(2))
//              .when(xpath("person/city = 'London'"))  // hvv - Hamburg
                    .to("activemq:topic:hvv.device.in?connectionFactory=#activeMqConnectionFactory")
//                .otherwise()
//                .to("activemq:topic:gts.device.in?connectionFactory=#activeMqConnectionFactory")
                .end();


                from("activemq:topic:hvv.device.in?connectionFactory=#activeMqConnectionFactory")
                .to("mock:result");
            }
        });

//        context.addRoutes(new RouteBuilder() {
//            public void configure() throws Exception {
//                from("activemq:queue:device.in?connectionFactory=#activeMqConnectionFactory")
//                .process(new Processor() {
//                    public void process(Exchange exchange) throws Exception {
//                        Device device = (Device) exchange.getIn().getBody();
//                        LOG.info("received jpa.Device {} with {} positions.", 
//                                device.getUniqueid(), device.getPositions().size());
//                    }
//                })
//                .to("mock:result");
//            }
//        });

//      now send message from client
        MockEndpoint mock = getMockEndpoint("mock:result");
        Traccar.Acknowledge response = (Traccar.Acknowledge) template   // Traccar.Device
                .requestBody("netty4:tcp://localhost:5200?clientInitializerFactory=#ack&sync=true", 
                        org.jeets.protocol.util.Samples.createDeviceWithPositionWithOneEvent());

        LOG.info("client received response: " + response);              //     jpa.Device
        assertEquals(789, response.getDeviceid());
        
        mock.expectedMessageCount(1);
        assertMockEndpointsSatisfied();

        Exchange message = mock.getExchanges().get(0);
        Device device = (Device) message.getIn().getBody();
        assertEquals(1, device.getPositions().size());
    }

    ActiveMQConnectionFactory activeMqConnectionFactory;

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        
//      settings from Main.main
        registry.bind("device", new DeviceProtoExtractor(null));    // request  to server
//      use Vm Transport for testing
        activeMqConnectionFactory = Main.getConnectionFactory(true);
        registry.bind("activeMqConnectionFactory", activeMqConnectionFactory);

//      cast to JMS spec
//      ConnectionFactory connectionFactory = activeMqConnectionFactory;
//      registry.bind("mqConnectionFactory", connectionFactory);

        // for response test
        registry.bind("ack", new AckProtoExtractor(null));           

        return registry;
    }

}
