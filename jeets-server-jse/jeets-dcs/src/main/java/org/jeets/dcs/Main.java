package org.jeets.dcs;

import org.apache.camel.CamelContext;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.jeets.dcs.steps.DeviceProtoExtractor;

/**
 * This DCS solution is as plain and simple that it doesn't even have a .to
 * endpoint. This method is simply starting the DCS as an integration test. If
 * you plan to use the DCS you can look at the ETL (and other projects) to find
 * how to use this project internally.
 * 
 * @author kbeigl@jeets.org
 */
public class Main {

    public static void main(String args[]) throws Exception {

    	System.out.println("starting jeets-dcs ...");
        SimpleRegistry registry = new SimpleRegistry();
        CamelContext context = new DefaultCamelContext(registry);

//      props belong to CamelContext, set directly after creation
        PropertiesComponent prop = context.getComponent("properties", PropertiesComponent.class);  
        prop.setLocation("classpath:dcs.properties");
//      System.out.println("configure Camel route from " +
//			context.resolvePropertyPlaceholders("{{dcs.host}}") + ":" +
//			context.resolvePropertyPlaceholders("{{dcs.protobuffer.port}}") + "//" +
//			context.resolvePropertyPlaceholders("{{dcs.protobuffer.protocol}}"));

        String protocolname = context.resolvePropertyPlaceholders("{{dcs.protobuffer.protocol}}");
        registry.put(protocolname, new DeviceProtoExtractor(null));
//      registry.put("ack", new ClientAckProtoExtractor(null));

        context.addRoutes(new DcsRoute() );
        context.start();
    }
    
}
