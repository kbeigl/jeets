package org.jeets.traccar;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

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
//	currently most methods are public. Visibility should be reduced..

    private static final Logger LOG = LoggerFactory.getLogger(TraccarSetup.class);

    public static Map<String, NettyServer> createServers(Map<Integer, Class<?>> protocolClasses) {
		
        // <"protocolName-transport", server>
		Map<String, NettyServer> servers = new HashMap<String, NettyServer>();
    	int udpServers = 0, tcpServers = 0;
        if (protocolClasses.size() > 0) {
            for (int port : protocolClasses.keySet()) {

            	// TODO handle java.net.BindException: Address already in use: bind
            	// collect all problematic ports first, assign available ports 
            	// and then provide free port with port finder and modify RAM config!?
                // netstat -ant > PID > Dienste: Plattformdienst für verbundene Geräte
				if (port == 5040)
					continue;

                @SuppressWarnings("unchecked")
                Class<? extends BaseProtocol> protocolClass = (Class<? extends BaseProtocol>) protocolClasses.get(port);
                String className = protocolClass.getSimpleName(); // TeltonikaProtocol > teltonika
                String protocolName = className.substring(0, className.length() - 8).toLowerCase();
            	BaseProtocol protocolInstance = instantiateProtocol(protocolClass);

				// Each *Protocol instance provides ONE or TWO TrackerServer instances for
            	// tcp AND/OR udp transport. After object instantiation each server holds
            	// a configured BasePipelineFactory and is used to get a ServerInitializerFactory 
            	// and register the instance for the camel-netty life cycle.
            	for (TrackerServer server : protocolInstance.getServerList()) {
            		NettyServer nettyServer = new NettyServer();
            		nettyServer.protocolName = protocolName;
            		nettyServer.transport = server.isDatagram() ? "udp" : "tcp";
            		nettyServer.factory   = server.getServerInitializerFactory();
            		nettyServer.port = port;

                    String protocolSpec = protocolName + "-" + nettyServer.transport;
            		servers.put(protocolSpec, nettyServer);
            		LOG.debug("added server: " + protocolSpec); // IMPLEMENT server.toString

//                  collect statistics
					if (nettyServer.transport.equals("udp")) {
						udpServers++;
					} else {
						tcpServers++;
					}
            	}
            }
			LOG.info("added {} UDP servers and {} TCP servers (total: {})", 
					udpServers, tcpServers, udpServers + tcpServers);
        } else {
            LOG.warn("No classes found matching the configFile");
        }
		return servers; // can be empty
	}

    /**
	 * Loading and instantiating the Protocol class can be separated by ClassGraph.
	 */
    public static BaseProtocol instantiateProtocol(Class<? extends BaseProtocol> protocolClass) {
        BaseProtocol protocol = null;
        try {
            protocol = (BaseProtocol) protocolClass.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | 
				InvocationTargetException | NoSuchMethodException | SecurityException e) {
            LOG.error("{} could not be instantiated!", protocolClass);
            e.printStackTrace();
        }
        return protocol;
    }

    /**
     * Scan class path with ClassGraph and only return the classes configured in
     * configFile. Scan is restricted to org.traccar.BaseProtocol packages inside
     * jeets- jars. (also verify for java -jar execution!)
     * <p>
     * Note that ClassGraph is applied in a try .. resources block to handle
     * ClassInfo/List/s in it. The returned classes are loaded inside the method but
     * have not been initialized yet!
     */
    public static Map<Integer, Class<?>> loadProtocolClasses() throws Exception {

    	// if (!isContextInitialized()) // align with getConfiguredProtocolPort
    	// throw RuntimeException ...

    	Map<Integer, Class<?>> protocolClasses  = new HashMap<Integer, Class<?>>();
    	long start = System.currentTimeMillis();
    	// also see Build-time scanning in Maven for ClassGraph
    	try (
    			ScanResult result = new ClassGraph()
    			// this would also init unused protocols
    			// .initializeLoadedClasses()
    			// .verbose() // very verbose! Only use at dev time?
    			// .enableClassInfo() // implied below
    			.acceptPackages("org.traccar.protocol") // with subpackages
    			.acceptJars("jeets*.jar") // scan only jeets* sources !! verify with dcs.jar libs
    			.scan();
    			) 
    	{
    		ClassInfoList classInfos = result.getSubclasses("org.traccar.BaseProtocol").directOnly();
    		LOG.info("found {} BaseProtocol classes in {} millis", classInfos.size(), (System.currentTimeMillis() - start));

    		for (ClassInfo protocolClassInfo : classInfos) {
    			String className = protocolClassInfo.getSimpleName(); // TeltonikaProtocol
    			String protocolName = null;
    			protocolName = className.substring(0, className.length() - 8).toLowerCase(); // teltonika
    			int port = -1;
    			port = TraccarSetup.getProtocolPort(protocolName);
    			// load class only, if port exists
    			if (port == -1) {
//    				LOG.warn("port# for '{}' protocol is not defined in configuration file.", protocolName);
    			} else {
    				/*
    				 * ClassGraph: You should do all class loading through ClassGraph, using
    				 * ClassInfo#loadClass() or ClassInfoList#loadClasses(), and never using
    				 * Class.forName(className), otherwise you may end up with some classes loaded
    				 * by the context class loader, and some by another class loader. This can cause
    				 * ClassCastException or other problems at weird places in your code.
    				 */
    				protocolClasses.put(port, protocolClassInfo.loadClass());
    				// class is loaded, but not yet initialized, nor instantiated!
    				LOG.debug("loaded class: {}\tname: {}\tport#{}", className, protocolName, port);
    			}
    		}
    	}
    	LOG.info("loaded " + protocolClasses.size() + " configured classes.");
    	return protocolClasses; // can be empty, i.e. size = 0
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
//    	make method private and imply in the above
        if (!isContextInitialized()) {
//          Context is not initialized yet, do now
            try {
                LOG.info("initializing traccar.Context with {}", configFile); 
                Context.init(configFile);
            } catch (Exception ex) { 
//              TODO throw IOException explicitly to provide infos
                LOG.error("Traccar Context might not be initialized and is mandatory!");
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

    /**
     * Default to localhost
     * <p>
     * camel-netty and/or spring are/is tedious about localhost, which doesn't
     * accept external access (in ubuntu). On the remote system 0.0.0.0 should be
     * used instead of 127.0.0.1.
     */
    private String host = "0.0.0.0";

}
