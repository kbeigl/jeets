package org.jeets.dcs.traccar;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.jeets.model.traccar.jpa.Device;
import org.jeets.protobuf.Traccar;

public class TraccarRoute extends RouteBuilder {
//  plain Camel, no Spring!
//  with camel-endpointdsl: extends EndpointRouteBuilder {

    @Override
    public void configure() throws Exception {

//      with camel-endpointdsl
//      from( netty("tcp://localhost:5200").sync(true) )
        from("netty:tcp://localhost:5200?serverInitializerFactory=#protobuffer&sync=true")
//      org.traccar.model.Position ?? traccar.proto to Device.class
        .convertBodyTo(Device.class)    // check exchange.getIn/Out
        .inOnly("seda:jeets-dcs?concurrentConsumers=4&waitForTaskToComplete=Never")
        .process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Device devEntity = (Device) exchange.getIn().getBody(Device.class);
//              LOG.info("DcsProcessor received Device: {} at {}", devProto.getUniqueid(), new Date().getTime());

                Traccar.Acknowledge.Builder ackBuilder = Traccar.Acknowledge.newBuilder();
//              ackBuilder.setDeviceid(devEntity.getUniqueid());
                ackBuilder.setDeviceid(789);
                exchange.getOut().setBody(ackBuilder.build(), Traccar.Acknowledge.class);
            }
        });

    }
}
