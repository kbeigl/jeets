package org.jeets.georouter;

import org.apache.camel.builder.RouteBuilder;
import org.jeets.model.traccar.jpa.Device;

public class DcsRoutes extends RouteBuilder {

    /* TODO: align Camel URIs (system properties) 
     * between modules and queues, topics to bind them.
     * "activemq:queue:device.in"
     * "jeets-mq:queue:device.in"   
     * add {{}} like in localhost:{{port}}
     */
    static final int PORT = Integer.parseInt(System.getProperty("port", "5200"));

    public void configure() throws Exception {
//      @formatter:off
        from("netty4:tcp://localhost:" + PORT + "?serverInitializerFactory=#device&sync=true")
        .convertBodyTo(Device.class)
        .inOnly("activemq:device.in?connectionFactory=#activeMqConnectionFactory")
        .process(new AckResponder());
//      @formatter:on
    }

}
