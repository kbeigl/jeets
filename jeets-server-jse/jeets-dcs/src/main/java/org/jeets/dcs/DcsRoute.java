package org.jeets.dcs;

import org.apache.camel.builder.RouteBuilder;
import org.jeets.model.traccar.jpa.Device;

public class DcsRoute extends RouteBuilder { // plain Camel without Swing!

//  private static final Logger LOG = LoggerFactory.getLogger(DcsRoute.class);
    static final int PORT = Integer.parseInt(System.getProperty("port", "5200"));

    public void configure() throws Exception {
        from("netty4:tcp://localhost:" + PORT + "?serverInitializerFactory=#device&sync=true")
//      Type Converter
        .convertBodyTo(Device.class)    // check exchange.getIn/Out
//      Message Translator
        .inOnly("seda:jeets-dcs?concurrentConsumers=4&waitForTaskToComplete=Never")
        .process(new AckResponder());    // only creates new Ack(789)
    }
    
}
