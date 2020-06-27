package org.traccar.protocol;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.jeets.model.traccar.jpa.Device;
import org.jeets.protobuf.Jeets;

public class TraccarRouteX extends RouteBuilder {

    private final String from;
    private final String routeId;

    public TraccarRouteX(String from, String routeId) {
        this.from = from;
        this.routeId = routeId;
    }

    @Override
    public void configure() throws Exception {
        from(from)
        .routeId(routeId)

        .log("DCS out: ${body}")        // proto Device
        .convertBodyTo(Device.class)
        .log("DCS out: ${body}")        //   jpa Device 

        .process(new Processor() {
            public void process(Exchange exchange) throws Exception {
//              Device device = exchange.getIn().getBody(Device.class);
//              System.out.println("persist jpa.Device " + device.getUniqueid()
//                  + " with " + device.getPositions().size() + " positions.");

                Jeets.Acknowledge.Builder ackBuilder = Jeets.Acknowledge.newBuilder();
//              ackBuilder.setDeviceid(devEntity.getUniqueid());
                ackBuilder.setDeviceid(789);
                exchange.getOut().setBody(ackBuilder.build(), Jeets.Acknowledge.class);
            }
        })
//      .log("DCS ${body.protocol} output: position ( time: ${body.deviceTime} "
//          + "lat: ${body.latitude} lon: ${body.longitude} )")
        .to("direct:jeets.model.traccar.jpa.Device");
//      .inOnly("seda:jeets-dcs?concurrentConsumers=4&waitForTaskToComplete=Never")
    }
}
