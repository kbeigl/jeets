package org.jeets.traccar;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.component.netty.ServerInitializerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.traccar.BaseProtocol;
import org.traccar.Context;
import org.traccar.TrackerServer;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

/**
 * Proprietary setup for Traccar Netty Pipelines.
 * <p>
 * Each -protocols- project should supply a dedicated RouteBuilder with a
 * Consumer Endpoint (DCS output) which can be picked up by a dcs-manager.
 */
public class TraccarSetup {

    private static final Logger LOG = LoggerFactory.getLogger(TraccarSetup.class);

    /**
     * Scan class path with ClassGraph and only return the classes configured in
     * configFile. Scan is restricted to org.traccar.BaseProtocol packages inside
     * jeets- jars. (also verify for java -jar execution!)
     * <p>
     * Note that ClassGraph is applied in a try .. resources block to handle
     * ClassInfo/List/s in it. The returned classes are loaded inside the method but
     * have not been initialized yet!
     */
    public static Map<Integer, Class<?>> getConfiguredBaseProtocolClasses() throws Exception {
        
//      if (!isContextInitialized()) // align with getConfiguredProtocolPort
//            throw RuntimeException ...

        Map<Integer, Class<?>> protocolClasses  = new HashMap<Integer, Class<?>>();
        long start = System.currentTimeMillis();
//      also see Build-time scanning in Maven for ClassGraph
        try (
                ScanResult result = new ClassGraph()
//              this would also init unused protocols
//              .initializeLoadedClasses()
//              .verbose() // very verbose! Only turn on if DEBUG .. ?
//              .enableClassInfo() // implied below
                .acceptPackages("org.traccar.protocol") // with subpackages
                .acceptJars("jeets*.jar") // scan only jeets* sources !! verify with dcs.jar libs
                .scan();
        ) {
            ClassInfoList classInfos = result.getSubclasses("org.traccar.BaseProtocol").directOnly();
            LOG.info("Found {} BaseProtocol classes in {} millis", classInfos.size(), (System.currentTimeMillis() - start));

            String protocolName = null;
            int port = -1;
            for (ClassInfo protocolClassInfo : classInfos) {

                String className = protocolClassInfo.getSimpleName(); // TeltonikaProtocol
                protocolName = className.substring(0, className.length() - 8).toLowerCase(); // teltonika
//              load class only, if port exists
                port = TraccarSetup.getConfiguredProtocolPort(protocolName);

                if (port == -1) {
                    LOG.info("port# for '{}' protocol is not defined in configuration file.", protocolName);
                } else {
                    /*
                     * ClassGraph: You should do all class loading through ClassGraph, using
                     * ClassInfo#loadClass() or ClassInfoList#loadClasses(), and never using
                     * Class.forName(className), otherwise you may end up with some classes loaded
                     * by the context classloader, and some by another classloader. This can cause
                     * ClassCastException or other problems at weird places in your code.
                     */
                    protocolClasses.put(port, protocolClassInfo.loadClass());
//                  clazz is loaded, but not yet initialized!
                    LOG.info("loaded protocol: {}\tport#{}\tclass: {}", protocolName, port, className );
                }
            }
        }
        return protocolClasses; // can be empty, size = 0
    }
    
    /* Currently only creating "tcp" servers */
     public static ServerInitializerFactory createServerInitializerFactory(Class<? extends BaseProtocol> protocolClass) {
        BaseProtocol protocolInstance = instantiateProtocol(protocolClass);
        String transport = "tcp";
        TrackerServer server = getProtocolServer(transport, protocolInstance);
        // compose URI and attach to server.setCamelUri() !
        if (server == null) { // BaseProtocol.nameFromClass will be removed!
            LOG.warn("No server found for '{}:{}'", transport, BaseProtocol.nameFromClass(protocolClass));
            return null;
        }
        return server.getServerInitializerFactory();
    }

    /**
     * Pick udp or tcp server, if present.
     * <p>
     * Each BaseProtocol instance provides one or two TrackerServer instances for
     * tcp and/or udp transport. Each server holds a configured BasePipelineFactory
     * which is used to get a ServerInitializerFactory and register the instance for
     * the camel-netty life cycle.
     * 
     * @param transport - tcp or udp
     * @param protocol - protocol name as defined in configFile
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
     * Loading and instantiating the Protocol class is clearly separated in order to
     * apply ClassGraph.
     */
    private static BaseProtocol instantiateProtocol(Class<? extends BaseProtocol> protocolClass) {
        BaseProtocol protocol = null;
        try {
            protocol = (BaseProtocol) protocolClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            LOG.error("{} could not be instantiated!", protocolClass);
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
    public static int getConfiguredProtocolPort(String protocol) throws Exception {
        String protocolPortKey = protocol + ".port";

        if (isContextInitialized()) {
            if (Context.getConfig().hasKey(protocolPortKey)) {
                return Context.getConfig().getInteger(protocolPortKey);
            } else {
                LOG.debug("{} protocol port is not defined in Context (and config file?)", protocol); 
//              returns -1 below
            }
        } else {
            throw new Exception("Traccar Context was not initialized. "
                    + "Make sure to apply contextInit at application startup!");
        }

        return -1; // ?
    }

    /**
     * Always apply this method to Initialize Traccar Context to ensure that it is
     * only loaded once! Method has public access in order to supply configFile from
     * external environment and setup traccar.Context only once.
     * <p>
     * Method returns fast, if context already is initialized. Therefore it doesn't
     * harm to call it multiple times.
     * 
     * @param configFile - path with file String
     */
    public static void contextInit(String configFile) {
        if (!isContextInitialized()) {
//          Context is not initialized yet, do now
            try {
                LOG.info("Initializing traccar.Context with {}", configFile); 
                Context.init(configFile);
            } catch (Exception ex) { 
//              TODO throw IOException explicitly to provide infos
                LOG.error("Traccar Context could not be initialized and is mandatory!");
//              ex.printStackTrace();
//              RuntimeException or System.exit(status) ?
            }
        } // else return
    }

    /**
     * Traccar Setup methods will only work with an initialized traccar.Context.
     */
    private static boolean isContextInitialized() {
        try {
//          Context.getConfig().getString("event.enable");
            Context.getConfig().getString("whatever");
        } catch (NullPointerException npe) {
            return false;
        }
        return true;
    }

}
