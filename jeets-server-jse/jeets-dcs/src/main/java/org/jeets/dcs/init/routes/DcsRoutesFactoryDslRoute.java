package org.jeets.dcs.init.routes;

import org.apache.camel.builder.RouteBuilder;
import org.traccar.Context;

/**
 * This route initializes the static traccar.Context class with different
 * *Manager instances analog to the original call in traccar.Main:
 * Context.init(configFile). The Context class is static and can be accessed
 * from traccar original code. Therefore subsequent components do not need to
 * attach to this route.
 * <p>
 * After initializing the Context the DCS Routes Factory is triggered to create
 * one or two DC servers for individual ports.
 *
 * @author kbeigl@jeets.org
 */
public class DcsRoutesFactoryDslRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
//      implicit check if file exists, not actually loaded
        from("file:setup/?fileName=traccar.xml&noop=true&initialDelay=0")
        .routeId("DcsRoutesFactoryDslRoute")
        .startupOrder(0)
//      catch file not found ?
//      .log("loading file ${header.CamelFileName} ...")
        .log("Context.init( ${file:absolute.path} ) ...")
//      static class with static methods without registration
        .bean(Context.class, "init(${file:absolute.path})")
        .log("... Context initialized from DSL Route!")
//      must be registered (as Type)!
        .bean(DcsRoutesFactory.class, "createTraccarDcsRoutes")
        .log("DcsRoutesFactory finished creating DCS Routes")
//      ends, but does not stop route!
        .stop();
    }

}
