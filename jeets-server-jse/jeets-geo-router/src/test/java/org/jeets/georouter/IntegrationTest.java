package org.jeets.georouter;

import org.apache.camel.spring.Main;

import junit.framework.TestCase;

public class IntegrationTest extends TestCase {
    // Currently this test is only starting the application to check if it boots OK
    public void testSpringStart() throws Exception {
        // boot up Spring application context for 3 seconds to check that it works OK
        Main.main("-duration", "3s");
    }
}
