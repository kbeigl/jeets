package org.jeets.jee.activemq;

import javax.inject.Named;

import org.apache.camel.CamelContext;
//import org.jeets.model.traccar.jpa.Device;
//import org.jeets.model.traccar.util.Samples;

/**
 * Simple message generator to simulate incoming device message String!
 * (for the time being)
 * 
 * @author kbeigl@jeets.org
 */
@Named
public class MessageGenerator {

    public String generateMessageString(CamelContext camelContext) {
        
//        Device dev = Samples.createDeviceWithTwoPositions();
//        System.out.println("created device: " + dev);
        
        return "49.123 12.456";
    }
}
