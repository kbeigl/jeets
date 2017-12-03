package org.jeets.georouter;

import org.apache.camel.builder.RouteBuilder;
import org.jeets.georouter.nodes.GeoBasedRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeoBasedRoute extends RouteBuilder {

    // use Camel .log instead (check examples directory)
    private static final Logger LOG = LoggerFactory.getLogger(GeoBasedRoute.class);
    static String routeName = "GeoRoute";
    public static String startUri = "activemq:queue:device.in";
    static String cbrUriOne = "activemq:topic:hvv.device.in";
    static String cbrUriTwo = "activemq:topic:gts.device.in";

    public void configure() throws Exception {
        LOG.info("configure GeoRoutes .. ");

//        getContext().setTracing(true);
//      @formatter:off
//      pick up all device messages from DCSs
        from(startUri)
        .routeId(routeName)
//      distribute devices to Geozones via CBR EIP
        .process(new GeoBasedRouter())
        .choice()
            .when(header("senddevice").isEqualTo("hvv"))
                .to(cbrUriOne)      // JEE
            .otherwise()
                .to(cbrUriTwo);     // JSE
//      @formatter:on
    }

}
