package org.jeets.georouter;

import org.apache.camel.builder.RouteBuilder;
import org.jeets.georouter.nodes.GeoBasedRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeoBasedRoute extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(GeoBasedRoute.class);
    static String routeName = "GeoRoute";
    public static String startUri = "activemq:queue:device.in";
    static String cbrUriOne = "activemq:topic:hvv.device.in";
    static String cbrUriTwo = "activemq:topic:gts.device.in";
    private String wktPolygon = "POLYGON(())";
    private String targetTopic = "gts";

    public GeoBasedRoute(String referencePolygon, String topic) {
        wktPolygon = referencePolygon;
        targetTopic = topic;
    }
    
    public void configure() throws Exception {
        LOG.info("configure GeoRoutes .. ");
//        getContext().setTracing(true);
//      @formatter:off
        from(startUri)
        .routeId(routeName)
        .process(new GeoBasedRouter(wktPolygon, targetTopic))
        .choice()
            .when(header("senddevice").isEqualTo(targetTopic))
                .to(cbrUriOne)      // JEE
            .otherwise()
                .to(cbrUriTwo);     // JSE
//      @formatter:on
    }

}
