package org.jeets.dcs;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import org.traccar.model.Position;

/**
 * Receives all messages from DCS output at a Mock Endpoint where the messages
 * can be counted and inspected.
 * <p>
 * The Processor can also be used to check the output before it reaches the
 * test.
 */
@Component
public class TestRouteToMock extends RouteBuilder {

	@Override
    public void configure() throws Exception {
        from("direct:traccar.model") // "seda:jeets-dcs"
        .process(new Processor() {
            public void process(Exchange exchange) throws Exception {
                Object msg = exchange.getIn().getBody();
                Position position = (Position) msg;
                System.out.println("received Position " + position);
            }
        })
        .to("mock:result");
    }

}
