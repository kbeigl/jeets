package org.jeets.tests;

import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.jeets.routes.ContextInitDslRoute;
import org.junit.Test;
import org.traccar.jeets.Context;

public class ContextInitDslRouteTest extends CamelTestSupport {

    @Test
    public void testInitContextFromFile() throws Exception {

//      consume end of Route
        context.addRoutes(new RouteBuilder() {
            public void configure() throws Exception {
                from("direct:Context.initialized")
//              .log("result:\n ${body}")
                .to("mock:result");
            }
        });

        NotifyBuilder notify = new NotifyBuilder(context).whenDone(1).create();
        assertTrue(notify.matchesMockWaitTime());

        assertNotNull("Config was not loaded", Context.getConfig());
//      validate if default values are overridden
//      check if ports are available and other required props

        assertNotNull("DeviceManager was not loaded", Context.getDeviceManager());
        assertNotNull("IdentityManager was not loaded", Context.getIdentityManager());

        assertNotNull("MediaManager was not loaded", Context.getMediaManager());
        System.out.println("media.path: " + Context.getConfig().getString("media.path"));

        assertNotNull("ConnectionManager was not loaded", Context.getConnectionManager());
//      ServerManager should not be started (in addition to camel-netty)
        assertNull("ServerManager should NOT be loaded", Context.getServerManager());
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new ContextInitDslRoute();
    }
}
