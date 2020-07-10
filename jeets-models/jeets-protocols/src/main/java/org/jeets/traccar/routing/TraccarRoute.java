package org.jeets.traccar.routing;

import org.apache.camel.builder.RouteBuilder;

/**
 * DCS Routes for ALL ORIGINAL Traccar Protocols are directed to one output
 * Endpoint 'traccar.model' where the traccar.model objects can be picked up by
 * the system.
 * <p>
 * Note: Re-using the same routeId, will quietly stop and replace the earlier
 * route!
 */
//SpringRouteBuilder !?
//with camel-endpointdsl: extends EndpointRouteBuilder {
public class TraccarRoute extends RouteBuilder {
    private final String from;
    private final String routeId;

    public TraccarRoute(String from, String routeId) {
        this.from = from;
        this.routeId = routeId;
    }

    /**
     * At this point the serverInitializerFactory is configured with it's
     * &lt;name&gt; only as meaningless String. Only before the Route is
     * instantiated by a first message the serverInitializerFactory has to be
     * registered.
     * <p>
     * DCS Routes for ALL Protocols are directed to one output endpoint where the
     * traccar.model objects can be picked up by the system. Later a jeets.model
     * output for JPA specified Entities will be added as an alternative.
     */
    @Override
    public void configure() throws Exception {
        from(from)
//      using the same id again, will quietly stop and replace the earlier route!!
        .routeId(routeId)
//      .routeGroup("hello-group")
//      .startupOrder(order)
//      this log expects a org.traccar.model.Position !!
        .log("DCS ${body.protocol} output: position ( time: ${body.deviceTime} "
                + "lat: ${body.latitude} lon: ${body.longitude} )")

//      add choice direct/seda to jeets.props !
//      Fine tuning with seda endpoint etc.
//      .to("seda:traccar.model");
//      jeets-dcs:traccar.model  ;)
        .to("direct:traccar.model");
//      already set in sync=false (?):
//      .setExchangePattern(ExchangePattern.InOnly)
//      .to("seda:jeets-dcs?concurrentConsumers=4&waitForTaskToComplete=Never")
//      .inOnly("seda:jeets-dcs?concurrentConsumers=4&waitForTaskToComplete=Never")
    }
}
