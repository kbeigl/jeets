package org.jeets.dcsToAmq;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.jeets.model.traccar.jpa.Device;
import org.jeets.protocol.Traccar;

public class DcsToAmqRoute extends RouteBuilder {

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
            .log("device from Netty:\n ${body}") 

//          try something like this for jar-with-dependencies converter problem
//            see STATUS notes in pom.xml ...
//            .process(new Processor() { 
//                @Override public void process(Exchange exchange) throws Exception { 
//                    Traccar.Device deviceProto = exchange.getIn().getBody(Traccar.Device.class); 
//                    exchange.getIn().setBody(deviceProto, Traccar.Device.class); 
//                } 
//            })
            
            .convertBodyTo(Device.class)
            .log("device from Converter:\n ${body}") 
//          .inOnly("activemq:device.in?connectionFactory=#activeMqConnectionFactory")
            .inOnly("activemq:hvv.in?connectionFactory=#activeMqConnectionFactory")
        .process(new AckResponder());
//      @formatter:on
    }

}
