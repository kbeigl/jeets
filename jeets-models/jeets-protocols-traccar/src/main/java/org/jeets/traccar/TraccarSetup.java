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
 * Proprietary setup for Traccar Camel Netty Pipelines.
 * <p>
 * Each -protocols- project should supply a dedicated RouteBuilder with a
 * Consumer Endpoint (DCS output) which can be picked up by a DCS Manager.
 * <p>
 * The term '-Configured-' refers to the reduced traccar.xml file. It only holds
 * servers with additional testing material like protocols in the repos
 * jeets-data/device.send folder. These tests become part of the JeeTS build,
 * test and integration test runs.
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
    public static Map<Integer, Class<?>> loadConfiguredBaseProtocolClasses() throws Exception {
        
//      if (!isContextInitialized()) // align with getConfiguredProtocolPort
//            throw RuntimeException ...

        Map<Integer, Class<?>> protocolClasses  = new HashMap<Integer, Class<?>>();
        long start = System.currentTimeMillis();
//      also see Build-time scanning in Maven for ClassGraph
        try (
                ScanResult result = new ClassGraph()
//              this would also init unused protocols
//              .initializeLoadedClasses()
//              .verbose() // very verbose! Only use at dev time?
//              .enableClassInfo() // implied below
                .acceptPackages("org.traccar.protocol") // with subpackages
                .acceptJars("jeets*.jar") // scan only jeets* sources !! verify with dcs.jar libs
                .scan();
        ) {
            ClassInfoList classInfos = result.getSubclasses("org.traccar.BaseProtocol").directOnly();
            LOG.info("found {} BaseProtocol classes in {} millis", classInfos.size(), (System.currentTimeMillis() - start));

            for (ClassInfo protocolClassInfo : classInfos) {
                String className = protocolClassInfo.getSimpleName(); // TeltonikaProtocol
                String protocolName = null;
                protocolName = className.substring(0, className.length() - 8).toLowerCase(); // teltonika
                int port = -1;
                port = TraccarSetup.getConfiguredProtocolPort(protocolName);
//              load class only, if port exists
                if (port == -1) {
                    LOG.warn("port# for '{}' protocol is not defined in configuration file.", protocolName);
                } else {
					/*
					 * ClassGraph: You should do all class loading through ClassGraph, using
					 * ClassInfo#loadClass() or ClassInfoList#loadClasses(), and never using
					 * Class.forName(className), otherwise you may end up with some classes loaded
					 * by the context class loader, and some by another class loader. This can cause
					 * ClassCastException or other problems at weird places in your code.
					 */
                    protocolClasses.put(port, protocolClassInfo.loadClass());
//                  class is loaded, but not yet initialized, nor instantiated!
                    LOG.debug("loaded class: {}\tname: {}\tport#{}", className, protocolName, port);
                }
            }
        }
        LOG.info("loaded " + protocolClasses.size() + " configured classes.");
        return protocolClasses; // can be empty, i.e. size = 0
    }
    
    /**
	 * Each BaseProtocol class provides one or two TrackerServer instances for tcp
	 * and/or udp transport. After object instantiation each server holds a
	 * configured BasePipelineFactory and is used to get a ServerInitializerFactory
	 * and register the instance for the camel-netty life cycle.
	 * <p>
	 * Note that the ServerInitializerFactory is not registered and not associated
	 * to a port# at this point.
	 * 
	 * @param protocolClass - a Traccar *Protocol class
	 * @return Map&lt;transport: "udp" or "tcp", ServerInitializerFactory>
	 */
    public static Map<String, ServerInitializerFactory> createServerInitializerFactories(Class<? extends BaseProtocol> protocolClass) {
//    	maybe add/replace method to create one Map for all protocols ?
    	BaseProtocol protocolInstance = instantiateProtocol(protocolClass);
    	LOG.info("get servers for '{}' protocol", BaseProtocol.nameFromClass(protocolClass));

		final Map<String, ServerInitializerFactory> serverInitializerFactories = new HashMap<String, ServerInitializerFactory>();
    	for (TrackerServer server : protocolInstance.getServerList()) {
    		String transport = server.isDatagram() ? "udp" : "tcp";
    		serverInitializerFactories.put(transport, server.getServerInitializerFactory());
//    		what about possible server hosts in the configuration?
//          String serverHost = (server.getAddress() == null) ? host : server.getAddress();
        }
    	return serverInitializerFactories;
    }

    /**
	 * Loading and instantiating the Protocol class is separated in order to apply
	 * ClassGraph.
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
                LOG.info("initializing traccar.Context with {}", configFile); 
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
//      !?  Context.getConfig();
        } catch (NullPointerException npe) {
            return false;
        }
        return true;
    }

}
