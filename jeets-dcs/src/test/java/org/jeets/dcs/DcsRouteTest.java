package org.jeets.dcs;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.jeets.dcs.steps.DeviceProtoExtractor;
import org.jeets.model.traccar.jpa.Device;
import org.jeets.protocol.Traccar;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DcsRouteTest extends CamelTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(DcsRouteTest.class);

    @Test
    public void testDcsRoute() throws Exception {
        LOG.info("create and add DcsRoute ...");
        context.addRoutes(new DcsRoute() );     // define routeId in DcsRoute !?
//      add Route Endpoint Consumer of Entity, i.e. database, MQ, etc
        context.addRoutes(new RouteBuilder() {
            public void configure() throws Exception {
//              this route segment is processed by a pool of five threads
                from("seda:jeets-dcs")
//              from("direct:jeets-dcs")
//              .log("seda out: ${body}")
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        
//                      receives Acknowledge ?! instead of...
                        Device device = (Device) exchange.getIn().getBody();
                        LOG.info("persist jpa.Device {} with {} positions.", 
                                device.getUniqueid(), device.getPositions().size());
//                      ShutdownTask starts with a timeout of 10 seconds!
                        Thread.sleep(5000);
                        LOG.info("... continue after 5 seconds ...");
//                        exchange.getOut().setBody("successfully ended process");
                   }
                })
                .to("mock:result");
            }
        });
        MockEndpoint mock = getMockEndpoint("mock:result");

//      start the test
        Traccar.Acknowledge response = (Traccar.Acknowledge) template
//              .requestBody("netty4:tcp://localhost:{{port}}?clientInitializerFactory=#ack&sync=true", createProtoDevice());
                .requestBody("netty4:tcp://localhost:5200?clientInitializerFactory=#ack&sync=true", createProtoDevice());
        LOG.info("client received response: " + response);
        assertEquals(789, response.getDeviceid());
        
        mock.expectedMessageCount(1);
//        mock.expectedMinimumMessageCount(1);
//        List<Exchange> messages = mock.getExchanges();
        assertMockEndpointsSatisfied();
//        printExchangeList(messages);
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        registry.bind("device", new DeviceProtoExtractor(null));    // request  to server
        registry.bind("ack", new ClientAckProtoExtractor(null));    // response to client
        return registry;
    }

//  TODO: replace with ..Samples
    private Traccar.Device createProtoDevice() {
        ProtoBean protoMaker = new ProtoBean();
        Traccar.Device deviceProto = protoMaker.createProtoDevice();
        return deviceProto;
    }

}
