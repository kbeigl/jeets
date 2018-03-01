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
        .bean("orderGenerator", "generateOrderString")
            .convertBodyTo(String.class)
            // Remove headers to ensure we end up with unique file names being generated in the next route
            .removeHeaders("*")
        .to("activemq:queue:OrdersQueue");
        
        from("activemq:queue:OrdersQueue")
        .choice()
            .when(simple("${body} == 'UK'"))
                .log("Sending order ${body} to the UK")
//                .to("file:{{jboss.server.data.dir}}/orders/processed/UK")
                .when(simple("${body} == 'US'"))
                .log("Sending order ${body} to the US")
//                .to("file:{{jboss.server.data.dir}}/orders/processed/US")
            .otherwise()
                .log("Sending order ${body} to another country")
//                .to("file://{{jboss.server.data.dir}}/orders/processed/Other")
        ;
        
    }
    
}
