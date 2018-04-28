package org.jeets.jee.activemq;

import javax.inject.Named;

import org.apache.camel.CamelContext;
import org.jeets.model.traccar.jpa.Device;
import org.jeets.model.traccar.util.Samples;

/**
 * Simple message generator to simulate incoming device message
 * - for the time being.
 * <p>
 * TODO: move to test environment
 * 
 * @author kbeigl@jeets.org
 */
@Named
public class MessageGenerator {
    
    public Device generateDeviceMessage(CamelContext camelContext) {
        Device dev = Samples.createDeviceWithTwoPositions();
        System.out.println("created device: " + dev);
        return dev;
    }
    
}
