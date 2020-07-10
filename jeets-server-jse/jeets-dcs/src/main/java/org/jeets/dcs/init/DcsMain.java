package org.jeets.dcs.init;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;
import org.jeets.dcs.init.routes.ConsumerDslRoute;
import org.jeets.dcs.init.routes.DcsRoutesFactory;
import org.jeets.dcs.init.routes.DcsRoutesFactoryDslRoute;
import org.jeets.dcs.init.tracking.TrackingSystem;

/**
 * Main class to initialize static traccar.Context, create and run DC servers on
 * all (available) ports. Shutdown with CTRL + C
 */
public class DcsMain extends Main {

    public static void main(String[] args) throws Exception {

        DcsMain main = new DcsMain();
//      name irrelevant, looked up by Type?
        main.bind("dcsRoutesFactory", new DcsRoutesFactory());

        RouteBuilder dcsRoutesFactory = new DcsRoutesFactoryDslRoute();
        main.configure().addRoutesBuilder(dcsRoutesFactory);

//      consume DCS messages
        main.configure().addRoutesBuilder(new ConsumerDslRoute());
        main.bind("trackingSystem", new TrackingSystem());

        main.run(args); // see built in args below
    }

}
