package org.jeets.tests;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.jeets.protocol.Traccar;
import org.jeets.protocol.util.Samples;
import org.jeets.routes.ClientInitializer;
import org.jeets.routes.ContextInitDslRoute;
import org.jeets.routes.ProtobufferDslRoute;
import org.jeets.util.MultiRegistry;
import org.junit.Test;
import org.traccar.jeets.BasePipelineFactory;
import org.traccar.jeets.protocol.ProtobufferProtocol;

/**
 * Extensive testing on all phases to startup and use only the single
 * Protobuffer Protocol Route. Serves as a template for a higher level loop over
 * *all* Protocols (and ports) to create DCS routes (to be launched with
 * spring.Main).
 *
 * @author kbeigl@jeets.org
 */
public class ProtobufferDslRouteTest extends CamelTestSupport {

    /**
     * By applying the CamelTestSupport methods createRegistry() and
     * createRouteBuilder() the test would be static, since Registering and
     * RouteBuilding happens before test starts.
     * <p>
     * For the dcs-manager we need to register the serverInitializerFactory before
     * the Route is build with a reference to it in it.
     * <p>
     * This test sequentially goes through addRoute - register - addRoute .. at
     * runtime!
     */
    @Test
    public void testSendingProtobufferDevice() throws Exception {

//    	start as first route, run once and stop
        context.addRoutes(new ContextInitDslRoute());

//      what's the standard? instantiate and keep reference or anonymous like in .bind ?
//      registry.bind("protobuffer", new ProtobufferProtocol()...getPipelineFactory());
        BasePipelineFactory bpf = new ProtobufferProtocol().getServerList().iterator().next().getPipelineFactory();
        MultiRegistry.addToRegistry(context.getRegistry(), "protobuffer", bpf);
        System.out.println("protobuffer registered");

//      now retrieve traccar.model messages via netty4:..
        context.addRoutes(new ProtobufferDslRoute());
        
//      consume traccar.model messages from netty4
        context.addRoutes(new RouteBuilder() {
            public void configure() throws Exception {
                from("direct:traccar.model")
                .routeId("ProtocolOutput")
                .log("system input: position ( id: ${body.deviceId} time: ${body.deviceTime} lat: ${body.latitude} lon: ${body.longitude} )")
                .to("mock:init");
            }
        });

        Thread.sleep(5*1000);

//      MockEndpoint mock = getMockEndpoint("mock:result");
        MultiRegistry.addToRegistry(context.getRegistry(), "ack", new ClientInitializer(null));

        Traccar.Acknowledge response = (Traccar.Acknowledge) template.requestBody(
                "netty4:tcp://localhost:5200?clientInitializerFactory=#ack&sync=true", createProtoDevice());

//      add assertions ;)

        System.out.println("response to client: " + response);
        
//      some time for logging in MainEventHandler before tearDown
        Thread.sleep(5*1000);

//      mock.expectedMessageCount(1);
//      mock.expectedMinimumMessageCount(1);
//      List<Exchange> messages = mock.getExchanges();
//      assertMockEndpointsSatisfied();
    }
    
    public static Traccar.Device createProtoDevice() {
        Traccar.Device.Builder deviceBuilder = Samples.createDeviceWithPositionWithOneEvent();
        assertEquals(Samples.uniqueId, deviceBuilder.getUniqueid());
        return deviceBuilder.build();
    }

}
