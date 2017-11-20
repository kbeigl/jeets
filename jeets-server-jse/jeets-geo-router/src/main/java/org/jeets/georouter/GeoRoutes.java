package org.jeets.georouter;

import org.apache.camel.spring.SpringRouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeoRoutes extends SpringRouteBuilder {

    // use Camel .log instead (check examples directory)
    private static final Logger LOG = LoggerFactory.getLogger(GeoRoutes.class);

    public void configure() throws Exception {
        LOG.info("configure GeoRoutes .. ");    // log sample

        getContext().setTracing(true);

//      @formatter:off
//      simulates DCS
        from("file:src/data?noop=true")     // multiple!? source files
//      DCS output device.in
        .to("activemq:queue:device.in");

//      actual geo-router
        from("activemq:queue:device.in")
            .choice()
            .when(xpath("person/city = 'London'"))  // hvv - Hamburg
                .to("activemq:topic:hvv.device.in")
            .otherwise()
                .to("activemq:topic:gts.device.in");  // any device not in Hamburg

//      Starting points for succeeding jeets-modules
        from("activemq:topic:hvv.device.in").to("file:target/devices/hvv");     // to WildFly
        from("activemq:topic:gts.device.in").to("file:target/devices/gts");   // distribute to *Handlers and *Managers
//      @formatter:on
    }

}
