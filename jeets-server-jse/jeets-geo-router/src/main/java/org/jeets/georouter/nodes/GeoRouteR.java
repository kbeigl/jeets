package org.jeets.georouter.nodes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Route device messages to Hamburg HVV or otherwise GTS.
 * Apply JTS for agencies boundaries.
 * 
 * @author kbeigl@jeets.org
 */
public class GeoRouteR  implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(GeoRouteR.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        
        // evaluate with JTS
        
        // set header("senddevice").isEqualTo("hvv"))
        
    }


}
