package org.jeets.dcs.traccar;

import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.traccar.Context;

public class TraccarContextTest extends CamelTestSupport {

    @Test
    public void testContextInit() throws Exception {

        Context.init("./setup/traccar.xml");
        
        assertNotNull("Config was not loaded", Context.getConfig());
//      validate if default values are overridden
//      check if ports are available and other required props

        assertNotNull("DeviceManager was not loaded", Context.getDeviceManager());
        assertNotNull("IdentityManager was not loaded", Context.getIdentityManager());

        assertNotNull("MediaManager was not loaded", Context.getMediaManager());
        System.out.println("media.path: " + Context.getConfig().getString("media.path"));

        assertNotNull("ConnectionManager was not loaded", Context.getConnectionManager());
//      ServerManager should NOT be started (in addition to camel-netty)
        assertNull("ServerManager should NOT be loaded", Context.getServerManager());
    }

}
