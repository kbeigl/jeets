package org.jeets.tests;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.jeets.dcs.traccar.routes.DcsRoutesFactory;
import org.jeets.protocol.Traccar;
import org.jeets.protocol.util.Samples;
import org.jeets.routes.ClientInitializer;
import org.jeets.routes.ContextInitDslRoute;
import org.jeets.util.MultiRegistry;
import org.junit.Test;

public class DcsRoutesFactoryTest extends CamelTestSupport {
    
    @Test
    public void testDcsRoutesFactory() throws Exception {
        
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
//              wait 5 seconds for ContextInitDslRoute
                from("timer://createRoutes?repeatCount=1&delay=5000")
//              assert Context initialized .. ?
                .routeId("createDcsRoutes")
                .bean(DcsRoutesFactory.class, "createTraccarDcsRoutes")
                .log("finishedDcsRoutes")
                .to("mock:finishedDcsRoutes");
            }
        });

//      no output from("direct:traccar.model") !?
//      analyze pipeline and return values in static 
//      ProtobufferDslRouteTest.testSendingProtobufferDevice() first
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:traccar.model")
                .log("receiving message: ${body}")
                .to("mock:messages");
            }
        });

//      start polling route
        context.addRoutes(new ContextInitDslRoute());

//      give DCS Routes Factory some time to setup before testing:
//      optimize waiting time with Notifier/s ...
        Thread.sleep(15*1000);

        getMockEndpoint("mock:messages").expectedMessageCount(2);

        MultiRegistry.addToRegistry(context.getRegistry(), "ack", new ClientInitializer(null));
        Traccar.Acknowledge response = (Traccar.Acknowledge) template.requestBody(
                "netty4:tcp://localhost:5200?clientInitializerFactory=#ack&sync=true", createProtoDevice());
        System.out.println("response to client: " + response);

        assertMockEndpointsSatisfied();

    }
    
    private Traccar.Device createProtoDevice() {
        Traccar.Device.Builder deviceBuilder = Samples.createDeviceWithTwoPositions();
        assertEquals(Samples.uniqueId, deviceBuilder.getUniqueid());
        return deviceBuilder.build();
    }
    
}
