package org.jeets.dcs.traccar;

import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.traccar.Context;

public class TraccarContextTest extends CamelTestSupport {

  /**
   * Initialize static traccar.Context. The Context class is static and can be accessed from
   * Traccar's original code.
   */
  @Test
  public void testContextInit() throws Exception {

    // TODO use TraccarSetup.. (and dcs.properties?)!
    // work around with relativ path
    Context.init("../../jeets-models/jeets-protocols-traccar/setup/traccar.xml");

    Assertions.assertNotNull(Context.getConfig(), "Config was not loaded");
    // validate if default values are overridden
    // check if ports are available and other required props

    Assertions.assertNotNull(Context.getDeviceManager(), "DeviceManager was not loaded");
    Assertions.assertNotNull(Context.getIdentityManager(), "IdentityManager was not loaded");

    Assertions.assertNotNull(Context.getMediaManager(), "MediaManager was not loaded");
    System.out.println("media.path: " + Context.getConfig().getString("media.path"));

    Assertions.assertNotNull(Context.getConnectionManager(), "ConnectionManager was not loaded");
    // ServerManager should NOT be started (in addition to camel-netty)
    // assertNull("ServerManager should NOT be loaded", Context.getServerManager());
  }
}
