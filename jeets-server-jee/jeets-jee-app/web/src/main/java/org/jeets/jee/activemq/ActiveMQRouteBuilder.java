package org.jeets.jee.activemq;

import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.ContextName;

@ApplicationScoped
@ContextName("camel-activemq-context")
public class ActiveMQRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("timer:order?period=30s&delay=0")
//      simple bean
        .bean("orderGenerator", "generateOrderString")
        .to("activemq:queue:OrdersQueue");
        
        from("activemq:queue:OrdersQueue")
//      enterprise java bean
        .to("ejb:java:global/jeets-jee-app/jeets-jee-ejb/GreeterEJB") // ..?method=sayHello"
//      .to("ejb:java:global/camel-ejb-ear/camel-ejb-sub-deployment/HelloBean");
        .choice()
            .when(simple("${body} == 'UK'"))
                .log("Sending order ${body} to the UK")
//              .to("file:{{jboss.server.data.dir}}/orders/processed/UK")
                .when(simple("${body} == 'US'"))
                .log("Sending order ${body} to the US")
//              .to("file:{{jboss.server.data.dir}}/orders/processed/US")
            .otherwise()
                .log("Sending order ${body} to another country")
//              .to("file://{{jboss.server.data.dir}}/orders/processed/Other")
        ;
        
    }

}
