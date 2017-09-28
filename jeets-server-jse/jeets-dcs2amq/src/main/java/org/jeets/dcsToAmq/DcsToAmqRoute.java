package org.jeets.dcsToAmq;

import org.apache.camel.builder.RouteBuilder;
import org.jeets.model.traccar.jpa.Device;

public class DcsToAmqRoute extends RouteBuilder {

    static final int PORT = Integer.parseInt(System.getProperty("port", "5200"));

    public void configure() throws Exception {
        //CHECKSTYLE:OFF
        from("netty4:tcp://localhost:" + PORT + "?serverInitializerFactory=#device&sync=true")
        .convertBodyTo(Device.class)

//      use implicit Camel jms Component, to be replaced with "activemq:.."
//      inOnly -> does this override MQ auto acknowledge, problematic ?
        .inOnly("activemq:queue:device.in?connectionFactory=#activeMqConnectionFactory")
//      TO BE TESTED
//      ?concurrentConsumers=4  ?timeToLive= .. 

        .process(new AckResponder());
        //CHECKSTYLE:ON
    }

}
