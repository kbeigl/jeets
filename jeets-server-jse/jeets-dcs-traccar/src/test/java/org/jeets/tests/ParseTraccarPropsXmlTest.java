package org.jeets.tests;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class ParseTraccarPropsXmlTest extends CamelTestSupport {

    @Test
    public void testPortParser() throws Exception {

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file:setup/?fileName=traccar.xml&noop=true")
                .log("reading file ${header.CamelFileName}")
                .to("direct:xml");
            }
        });
        context.start();
        Thread.sleep(5000);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {

        return new RouteBuilder() {

            public void configure() throws Exception {
//              context.setTracing(true);

                from("direct:xml")
//              add Route Names
                .split(xpath("/properties")) // root element
                    .log("${body}")
                    .multicast() // to be decided (?)
                        .to("direct:protocol")
                .end();

                from("direct:protocol")
                .log("Split by entry Element")
                .split(xpath("/properties/entry"))
                    .log("${body}")
                .end();

//            .to("");
//            register *Decoders and
//                dynamically create netty4:protocolname route ..
            }
        };
    }
}
