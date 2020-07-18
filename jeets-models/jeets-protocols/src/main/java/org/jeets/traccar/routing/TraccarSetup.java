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

    private static final Logger LOG = LoggerFactory.getLogger(TraccarSetup.class);

    /* Currently only creating "tcp" servers */
    public static ServerInitializerFactory createServerInitializerFactory(Class<?> protocolClass) {
        BaseProtocol protocolInstance = instantiateProtocol(protocolClass);
        String transport = "tcp";
        TrackerServer server = getProtocolServer(transport, protocolInstance);
        // compose URI and attach to server.setCamelUri() !
        if (server == null) {
            LOG.warn("No server found for '" + transport + ":" + BaseProtocol.nameFromClass(protocolClass));
            return null;
        }
        return server.getServerInitializerFactory();
    }

    /**
     * Pick udp or tcp server, if present. <br>
     * merge with instantiateProtocol javadoc below
     * 
     * @param transport
     * @param protocol
     * @return
     */
    public static TrackerServer getProtocolServer(String transport, BaseProtocol protocol) {
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
     * Retrieve Traccar &ltprotocol&gt.port number from Context.
     * <p>
     * Prerequisite of calling this method is the {@link #contextInit} method on a
     * configuration file. If the port was not found in the Context port #-1 is
     * returned and should be handled by the caller. Else it will raise an Exception
     * when trying to bind it: <br>
     * <code>java.lang.IllegalArgumentException: hostname can't be null
     * at java.net.InetSocketAddress.checkHost(InetSocketAddress.java:149) </code>
     * 
     * @param protocol
     * @return port#
     * @throws Exception if the &ltprotocol&gt.port was not read into the Context
     *                   with {@link #contextInit} or is not defined in xml config
     *                   file
     */
    public static int getProtocolPort(String protocol) throws Exception {
        String protocolPortKey = protocol + ".port";
        try {
            if (Context.getConfig().hasKey(protocolPortKey)) {
                return Context.getConfig().getInteger(protocolPortKey);
            } else {
                LOG.debug(protocol + " protocol port is not defined in Context (and config file?)"); 
//              returns -1 below
            }
        } catch (NullPointerException npe) {
            throw new Exception("Traccar Context was not initialized. "
                    + "Make sure to apply contextInit at application startup!");
        }
        return -1; // ?
    }

    /**
     * Always apply this method to Initialize Traccar Context to ensure that it is
     * only loaded once!
     * <p>
     * Method returns fast, if context already is initialized. Therefore it doesn't
     * harm to call it multiple times.
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
                LOG.error("Traccar Context could not be initialized and is mandatory!");
//              ex.printStackTrace();
            }
        }
    }

}
