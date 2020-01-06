package org.jeets.device.console;

import org.apache.camel.spring.Main;

/**
 * A simple helper application for development to send HEX Strings to a server
 * and receive HEX Strings in request-response mode.
 */
public final class DeviceConsole {

    public DeviceConsole() {}

    public static void main(String[] args) throws Exception {
        // Main makes it easier to run a Spring application
        Main main = new Main();
        // configure the location of the Spring XML file
        main.setApplicationContextUri("META-INF/spring/camel-context.xml");
        // run and block until Camel is stopped (or JVM terminated)
        main.run();
    }

}
