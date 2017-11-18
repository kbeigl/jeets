package org.jeets.dcs;

import org.apache.camel.builder.RouteBuilder;
import org.jeets.model.traccar.jpa.Device;

public class DcsRoute extends RouteBuilder { // plain Camel without Swing!

//  private static final Logger LOG = LoggerFactory.getLogger(DcsRoute.class);
    static final int PORT = Integer.parseInt(System.getProperty("port", "5200"));

    public void configure() throws Exception {
        
        // to be tested (also see registry)
        // Protocol buffer route without ProtoExtractor
        // from("netty4:tcp://localhost:" + PORT + "?decoder=#decoder&sync=false").unmarshal()
        // .protobuf(SampleMessage.getDefaultInstance()) ...

        from("netty4:tcp://localhost:" + PORT + "?serverInitializerFactory=#device&sync=true")
//      TODO: add {{}} like in localhost:{{port}}
//      .log("proto: ${body}")
//      Type Converter
        .convertBodyTo(Device.class)    // check exchange.getIn/Out
//      Message Translator
//      .transform(constant( Traccar.Acknowledge.newBuilder().setDeviceid(888).build() ));
        .inOnly("seda:jeets-dcs?concurrentConsumers=4&waitForTaskToComplete=Never")
        .process(new AckResponder())     // only creates new Ack(789)
//      .inOnly("direct:jeets-dcs")
//      .to("seda:jeets-dcs")
        ;
    }
    
}
