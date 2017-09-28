package org.jeets.dcsToAmq;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.jeets.dcsToAmq.steps.DeviceProtoExtractor;

public class Main {

    public static void main(String args[]) throws Exception {
        
//      MAIN HAS NOT BEEN TESTED (COPY FROM DCS) - use spring.Main instead ..

        SimpleRegistry registry = new SimpleRegistry();
        registry.put("device", new DeviceProtoExtractor(null));
        CamelContext context = new DefaultCamelContext(registry);
//        context.addComponent("jeets-dcs", new DcsComponent());    // auto discovered
        context.addRoutes(new DcsToAmqRoute() );
        context.start();

    }
    
}
