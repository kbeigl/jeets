package org.jeets.traccar.routing;

import org.apache.camel.component.netty.ServerInitializerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.traccar.BaseProtocol;
import org.traccar.Context;
import org.traccar.TrackerServer;

/**
 * Proprietary setup for Traccar Netty Pipelines.
 * <p>
 * Consider moving these methods and routes to protocols-traccar. i.e. each
 * -protocols- project should supply a dedicated RouteBuilder with a Consumer
 * Endpoint (DCS output) which can be picked up by the dcs-manager.
 */
public class TraccarSetup {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraccarSetup.class);

    /* Currently only creating "tcp" servers */
    public static ServerInitializerFactory createServerInitializerFactory(Class<?> protocolClass) {
        // could do without:
        String protocolName = BaseProtocol.nameFromClass(protocolClass);
        BaseProtocol protocol = instantiateProtocol(protocolClass);
        String transport = "tcp";
        TrackerServer server = getProtocolServer(transport, protocol);
        // compose URI and attach to server.setCamelUri() !
        if (server == null) {
            LOGGER.warn("No server found for '" + transport + ":" + protocolName);
            return null;
        }
        return server.getServerInitializerFactory();
    }

    /**
     * Pick udp or tcp server, if present.
     */
    private static TrackerServer getProtocolServer(String transport, BaseProtocol protocol) {
        for (TrackerServer server : protocol.getServerList()) {
            if (transport.equals("udp") && server.isDatagram()) {
                return server;
            } else if (transport.equals("tcp") && !server.isDatagram()) {
                return server;
            }
        }
        return null;
    }

    /**
     * Each BaseProtocol instance provides one or two TrackerServer instances for
     * tcp and/or udp transport. Each server holds a configured BasePipelineFactory
     * which is used to get a ServerInitializerFactory and register the instance for
     * the camel-netty life cycle.
     */
    private static BaseProtocol instantiateProtocol(Class<?> protocolClass) {
        BaseProtocol protocol = null;
        try {
            protocol = (BaseProtocol) protocolClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            System.err.println(protocolClass + " could not be instantiated!");
            e.printStackTrace();
        }
        return protocol;
    }

    /**
     * Retrieve Traccar Protocol Port from Context.
     * <p>
     * Prerequisite of calling this method is the contextInit method with a
     * configuration file. If the port was not found in the Context port #-1 is
     * returned and should be handled by the caller.
     * 
     * @param protocol
     * @return
     * @throws Exception
     */
    public static int getProtocolPort(String protocol) throws Exception {
        String protocolPortKey = protocol + ".port";
        try {
            if (Context.getConfig().hasKey(protocolPortKey)) {
                return Context.getConfig().getInteger(protocolPortKey);
            } else {
                System.err.println(protocol + " is not defined in config file!"); 
//              "Port for " + protocolPort + " was not found in Context."
            }
        } catch (NullPointerException npe) {
            throw new Exception("Traccar Context was not initialized. Make sure to apply contextInit at application startup!");
        }
        return -1; // ?
    }

    /**
     * Always apply this method to Initialize Traccar Context to ensure that it is
     * only loaded once!
     * <p>
     * Method returns fast, if context already is initialized.
     * 
     * @param configFile
     */
    @SuppressWarnings("deprecation")
    public static void contextInit(String configFile) {
//      TODO: supply fallback values for jeets structure, i.e. mvn build and test
        try {
            Context.getConfig().getString("event.enable");
        } catch (NullPointerException npe) {
//          Context is not initialized yet, do now
            try {
                Context.init(configFile);
            } catch (Exception ex) {
                System.err.println("Traccar Context could not be initialized and is mandatory!"); // change to logger
//              ex.printStackTrace();
            }
        }
    }

}
