package org.jeets.georouter;

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeoRoutes extends RouteBuilder {

    // use Camel .log instead (check examples directory)
    private static final Logger LOG = LoggerFactory.getLogger(GeoRoutes.class);

    public void configure() throws Exception {
        LOG.info("configure GeoRoutes .. ");

        getContext().setTracing(true);

//      @formatter:off
//      from("activemq:queue:device.in")
        from("activemq:device.in?connectionFactory=#activeMqConnectionFactory")
        
        .choice()
        .when(xpath("person/city = 'London'"))  // hvv - Hamburg
            .to("activemq:topic:hvv.device.in")
        .otherwise()
            .to("activemq:topic:gts.device.in");  // any device not in Hamburg

//      Starting points for succeeding jeets-modules
//        from("activemq:topic:hvv.device.in").to("file:target/devices/hvv");     // to WildFly
//        from("activemq:topic:gts.device.in").to("file:target/devices/gts");   // distribute to *Handlers and *Managers
//      @formatter:on
    }

}
