package org.jeets.dcsToAmq;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;

public class CamelGeoRoutingTest extends CamelTestSupport {
        
//    @Test
    public void testDeviceFromNettyToAmqToMock() throws Exception {
            
//      from "netty4:tcp://localhost:{port}?serverInitializerFactory=#device&sync=true"
//      .to "activemq:queue:device.in" :
//      context.addRoutes(new DcsRoutes() );  from netty
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

/*      now send protocol message from client
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
 */
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        return super.createRegistry();
    }

}
