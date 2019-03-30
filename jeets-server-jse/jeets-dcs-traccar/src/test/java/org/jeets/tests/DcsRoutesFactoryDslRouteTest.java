package org.jeets.tests;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.jeets.dcs.traccar.routes.DcsRoutesFactoryDslRoute;
import org.jeets.protocol.Traccar;
import org.jeets.routes.ClientInitializer;
import org.jeets.util.MultiRegistry;
import org.junit.Test;

public class DcsRoutesFactoryDslRouteTest extends CamelTestSupport {

	@Test
	public void testDcsRouteCreation() throws Exception {

//      assert from traccar.model output for all tests
        context.addRoutes(new RouteBuilder() {
            public void configure() throws Exception {
                from("direct:traccar.model")
                .routeId("systemImput")
                .log("system.in:\n ${body}")
                .to("mock:assert");
            }
        });

//      Mock components can verify a rich variety of expectations, such as the following:
//      ¡ The correct number of messages are received on each endpoint.
//      ¡ The messages arrive in the correct order.
//      ¡ The correct payloads are received.
//      ¡ The test ran within the expected time period.
        MockEndpoint mockTest = getMockEndpoint("mock:assert");

//	    depends on nr of protocols to boot (and PC, OS etc.)
//	    improve with NotifyBuilder 
        Thread.sleep(10*1000);
	    System.out.println("Starting tests ...");

//      Protobuffer -------------------------------------------------
	    mockTest.expectedMessageCount(1);
        MultiRegistry.addToRegistry(context.getRegistry(), "ack", new ClientInitializer(null));
//      template.sendBody("jms:topic:quote", "Camel rocks");
        Traccar.Acknowledge response = (Traccar.Acknowledge) template.requestBody(
                "netty4:tcp://localhost:5200?clientInitializerFactory=#ack&sync=true", 
                ProtobufferDslRouteTest.createProtoDevice());
        System.out.println("response to client: " + response);

//      public void testDecode() throws Exception {
//      AdmProtocolDecoder decoder = new AdmProtocolDecoder(null);
//      verifyNull    (decoder, binary("010042033836313331313030323639343838320501000000000000000000000000000000000000000000000000000000000000000000000000000000000000000073"));
//      verifyPosition(decoder, binary("01002680336510002062A34C423DCF8E42A50B1700005801140767E30F568F2534107D220000"));

        mockTest.assertIsSatisfied();
        assertMockEndpointsSatisfied();
	}

	@Override
	protected RoutesBuilder createRouteBuilder() throws Exception {
		return new DcsRoutesFactoryDslRoute();
	}

}
