package org.jeets.camel.component.dcs;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.ScheduledPollConsumer;

/**
 * The Dcs consumer.
 */
public class DcsScheduledPollConsumer extends ScheduledPollConsumer {
    private final DcsEndpoint endpoint;

    public DcsScheduledPollConsumer(DcsEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
        System.out.println("new DcsConsumer: " + endpoint + " / processor: " + processor);
        this.endpoint = endpoint;
    }

    @Override
    protected int poll() throws Exception {
        System.out.println("DcsConsumer.poll ...");
        Exchange exchange = endpoint.createExchange();
        // create a message body OR LEAVE UNTOUCHED
        exchange.getIn().setBody("DcsConsumer String Body");
        System.out.println("Route: " + getRoute());;
        try {
            // send message to next processor in the route
            getProcessor().process(exchange);
            return 1; // number of messages polled
        } finally {
            // log exception if an exception occurred and was not handled
            if (exchange.getException() != null) {
                getExceptionHandler().handleException("Error processing exchange", exchange, exchange.getException());
            }
        }
    }
}
