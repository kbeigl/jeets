package org.jeets.georouter.nodes;

import org.apache.camel.Consume;
import org.apache.camel.Header;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendTrackToGeorouter {

    private static final Logger LOG = LoggerFactory.getLogger(SendTrackToGeorouter.class);

//  send to Georouter input
    @Produce(uri = "activemq:queue:device.in")
    ProducerTemplate producer;

    @Consume(uri = "file:src/test/resources/track?noop=true")
//    public void onFileSendToQueue(String body) {
    public void onFileSendToQueue(String body, @Header("CamelFileName") String name) {
        LOG.info("Incoming file: {}", body);
        
//      loop over track
//        create device from partial track
            producer.sendBody(body);
    }

}
