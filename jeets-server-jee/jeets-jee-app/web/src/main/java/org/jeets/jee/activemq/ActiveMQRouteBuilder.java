package org.jeets.jee.activemq;

import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.ContextName;

@ApplicationScoped
@ContextName("camel-activemq-context")
public class ActiveMQRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {

//      simulate external messages via activemq
        from("timer:order?period=30s&delay=0")
        .bean("messageGenerator", "generateMessageString")
        .to("activemq:queue:hvv.in");

        from("activemq:queue:hvv.in")
        .to("ejb:java:global/jeets-jee-app/jeets-jee-ejb/ApplicationBean?method=processMessage")
        .log("Send message ${body} to another bean (?)");
/*      instantiate vehicles as Stateful beans ..
        .choice()
            .when(simple("${body} == 'UK'"))
                .log("Sending order ${body} to the UK")
//              .to("file:{{jboss.server.data.dir}}/orders/processed/UK")
            .when(simple("${body} == 'US'"))
                .log("Sending order ${body} to the US")
            .otherwise()
                .log("Sending order ${body} to another country");
 */
    }

}
