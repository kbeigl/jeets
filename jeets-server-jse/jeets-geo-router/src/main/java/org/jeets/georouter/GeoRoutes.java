package org.jeets.georouter;

import org.apache.camel.spring.SpringRouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeoRoutes extends SpringRouteBuilder {

    // use Camel .log instead (check examples directory)
    private static final Logger LOG = LoggerFactory.getLogger(GeoRoutes.class);
    // "..device.port"
    static final int PORT = Integer.parseInt(System.getProperty("port", "5200"));

    public void configure() throws Exception {
        LOG.info("configure GeoRoutes .. ");
//      @formatter:off
//      getContext().setTracing(true);

//      from jms/amq ? device.in => DCS output
//      currently copies files with every test or start
        from("file:src/data?noop=true")
            .choice()
            .when(xpath("person/city = 'London'"))
//              .log ..
                .to("file:target/messages/uk")
            .otherwise()
                .to("file:target/messages/others");

//        .to("jms:incomingOrders");
        
//        from("jms:incomingOrders")
//            .choice()
//            .when(header("CamelFileName").endsWith(".xml"))
//                .to("jms:topic:xmlOrders")
//            .when(header("CamelFileName").regex("^.*(csv|csl)$"))
//                .to("jms:topic:csvOrders");
//            .otherwise ?
//        
//        from("jms:topic:xmlOrders").to("jms:accounting");
//        from("jms:topic:xmlOrders").to("jms:production");
//      @formatter:on
    }

}
