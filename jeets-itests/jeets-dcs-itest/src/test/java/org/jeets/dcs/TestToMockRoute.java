package org.jeets.dcs;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.traccar.model.Position;

/**
 * Receive messages from DCS output and forward them to a Mock Endpoint where
 * the messages can be counted and inspected.
 */
@Component
public class TestToMockRoute extends RouteBuilder {
	
    private static final Logger LOG = LoggerFactory.getLogger(TestToMockRoute.class);

	@Override
    public void configure() throws Exception {
        from("direct:traccar.model") // "seda:jeets-dcs"
        .process(new Processor() {
        	public void process(Exchange exchange) throws Exception {
        		Object msg = exchange.getIn().getBody();
        		Position pos = (Position) msg;
        		LOG.info("Position {} {} ({}, {}) from DCS to Mock ", 
        				pos.getProtocol(), pos.getFixTime(),
        				pos.getLatitude(), pos.getLongitude());
        	}
        })
        .to("mock:result");
    }
}
