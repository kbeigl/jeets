package org.jeets.camel.component.dcs;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;

/**
 * Represents a DCS Endpoint.
 */
@UriEndpoint(scheme = "jeets-dcs", title = "Dcs", syntax="jeets-dcs:name", consumerClass = DcsScheduledPollConsumer.class, label = "Dcs")
public class DcsEndpoint extends DefaultEndpoint {
    @UriPath @Metadata(required = "true")
    private String name;
    @UriParam(defaultValue = "10")
    private int option = 10;

    public DcsEndpoint() {
    }

    public DcsEndpoint(String uri, DcsComponent component) {
        super(uri, component);
    }

    public Producer createProducer() throws Exception {
        return new DcsProducer(this);
    }
    
    public Consumer createConsumer(Processor processor) throws Exception {
        Consumer consumer = new DcsScheduledPollConsumer(this, processor);
//      configureConsumer(consumer);
        return consumer;
    }

    public boolean isSingleton() {
        return true;
    }

    /**
     * Some description of this option, and what it does
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Some description of this option, and what it does
     * 
     * TODO ?host=localhost&port=5200&protocol=device
     */
    public void setOption(int option) {
        this.option = option;
    }

    public int getOption() {
        return option;
    }


}
