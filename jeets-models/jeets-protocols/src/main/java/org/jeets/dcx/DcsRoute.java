package org.jeets.dcx;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.jeets.model.traccar.jpa.Device;
import org.jeets.protobuf.Traccar;;;

public class DcsRoute extends RouteBuilder { // plain Camel without Swing!

    public void configure() throws Exception {
//		EL #{{ raises EL syntax error: Expecting expression, but creates %23 and works. Fix some time.
        from("netty:tcp://localhost:5200?serverInitializerFactory=#protobuffer&sync=true")
        .convertBodyTo(Device.class)    // check exchange.getIn/Out
        .inOnly("seda:jeets-dcs?concurrentConsumers=4&waitForTaskToComplete=Never")
//      .process(new AckResponder());    // only creates new Ack(789)
        .process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Device devEntity = (Device) exchange.getIn().getBody(Device.class);
//              LOG.info("AckResponder.process getIn jpa.Device.uniqueid " + devEntity.getUniqueid());
//              LOG.info("DcsProcessor received Device: {} at {}", devProto.getUniqueid(), new Date().getTime());

                Traccar.Acknowledge.Builder ackBuilder = Traccar.Acknowledge.newBuilder();
//              ackBuilder.setDeviceid(devEntity.getUniqueid());
                ackBuilder.setDeviceid(789);
                exchange.getOut().setBody(ackBuilder.build(), Traccar.Acknowledge.class);
            }
        });
    }
    
}
