package org.jeets.dcs;

import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.traccar.Context;

public class ContextTest extends CamelTestSupport {

//  hard coded paths and files, i.e. integral part of maven run !
    public static final String configuredServers = "./setup/traccar.xml";

//  TODO carefully implement allServers
//  ensure that previous xml configuration is erased
//  important to study static bahavior!
    public static final String allServers = "./setup/traccar.all.xml";

    @Test
    public void testContextInit() throws Exception {

        Context.init(configuredServers);
//      higher level ... more tests
//      TraccarSetup.contextInit(configFile);
        
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
