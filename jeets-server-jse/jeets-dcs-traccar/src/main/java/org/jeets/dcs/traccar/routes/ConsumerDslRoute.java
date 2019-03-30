package org.jeets.dcs.traccar.routes;

import org.apache.camel.builder.RouteBuilder;
import org.jeets.dcs.traccar.tracking.TrackingSystem;

/**
 * This route is a simple implementation of a System.in endpoint consuming
 * DCS.out messages in traccar.model types.
 * <p>
 * Route should be considered as a temporary and most simple consumer demo.
 *
 * @author kbeigl@jeets.org
 */
public class ConsumerDslRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:traccar.model")
        .routeId("DcsConsumerRoute")
        .log("receiving message: ${body}")
        .log("system input: position ( id: ${body.deviceId} time: ${body.deviceTime} "
                + "lat: ${body.latitude} lon: ${body.longitude} )")
        .bean(TrackingSystem.class, "messageArrived");
//      .to("bean:TrackingSystem?method=messageArrived");
//      .beanRef("myBean", "methodName");
    }

}
