package org.jeets.georouter;

import org.apache.camel.builder.RouteBuilder;
import org.jeets.georouter.nodes.GeoRouteR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeoRouteS extends RouteBuilder {

    // use Camel .log instead (check examples directory)
    private static final Logger LOG = LoggerFactory.getLogger(GeoRouteS.class);
    public String routeName = "GeoRoute";

    public void configure() throws Exception {
        LOG.info("configure GeoRoutes .. ");

        getContext().setTracing(true);
//      @formatter:off
//      pick up all device messages from DCSs
        from("activemq:queue:device.in")
        .routeId(routeName)
//      distribute devices to Geozones via CBR EIP
        .process(new GeoRouteR())
        .choice()
            .when(header("senddevice").isEqualTo("hvv"))
                .to("activemq:topic:hvv.device.in") // to WildFly
            .otherwise()
                .to("activemq:topic:gts.device.in");
//      @formatter:on
    }

}
