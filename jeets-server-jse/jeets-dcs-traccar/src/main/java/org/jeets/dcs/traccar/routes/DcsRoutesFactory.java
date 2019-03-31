package org.jeets.dcs.traccar.routes;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.Registry;
import org.jeets.util.ClassFinder;
import org.jeets.util.MultiRegistry;
import org.traccar.BaseProtocol;
import org.traccar.Context;
import org.traccar.TrackerServer;

/**
 * This Factory replaces the org.traccar.ServerManager
 * <p>
 * Traverse *Protocol classes in org.traccar.protocol and align with properties'
 * port from configuration file. Register protocol object and create route with
 * composed URI.
 *
 * @author kbeigl@jeets.org
 */
public class DcsRoutesFactory {

//  default for every server without specified address
    private final String host = "localhost";

    public void createTraccarDcsRoutes(CamelContext camelContext, Registry registry) {

        Map<String, BaseProtocol> protocolList = getInstantiatedProtocols("org.traccar.protocol");
        System.out.println("instantiated " + protocolList.size() + " *Protocol objects");
        Map<String, Integer> portFailures = new ConcurrentHashMap<>();
//      introduce counter before and after ? in test?
        for (String protocolName: protocolList.keySet()) {
            String serverInitializerFactory = protocolName;
//          register ONE serverIni instance for all protocols here? or for each server below?
//          Util.addToRegistry(registry, serverInitializerFactory, server(x).getPipelineFactory());
//          public AtrackProtocol() {
//          addServer(new TrackerServer(tcp, protocolName) {
//          addServer(new TrackerServer(udp, protocolName) {
//          if (protocolName.equals("protobuffer")) {   // single out
            BaseProtocol protocolObject = protocolList.get(protocolName);
            for (TrackerServer server : protocolObject.getServerList()) {
                String transport = server.isDatagram() ? "udp" : "tcp";
                String uri = "netty4:" + transport + "://"; // change to StringBuffer
                String serverHost = (server.getAddress() == null) ? host : server.getAddress();
                uri += serverHost + ":" + server.getPort() + "?";
                serverInitializerFactory = (transport.equals("tcp")) ? protocolName : protocolName + "-" + transport;
                uri += "serverInitializerFactory=#" + serverInitializerFactory;
//              sync=true for all protocols ? see TrackerServer.start:
//              Channel channel = bootstrap.bind(endpoint).sync().channel();
                uri += "&sync=true";

//              "&workerPool=#sharedPool&usingExecutorService=false" register in XML,
//              see .../camel-netty4/src/main/docs/netty4-component.adoc
//              register netty4 as jeets-dcs ;)

//              register serverIni for each protocol (or move before for loop?)
                System.out.println("register: " + serverInitializerFactory + " => " + protocolObject);
                MultiRegistry.addToRegistry(registry, serverInitializerFactory, server.getPipelineFactory());

                System.out.println("create Route '" + serverInitializerFactory + "' from(\"" + uri + "\") ...");
                try {
                    camelContext.addRoutes(new DcsRouteBuilder(camelContext, uri, serverInitializerFactory));
//                  add route counter in addition to: protocolList.size() + " *Protocol objects"
//                  may need to 'clean' Netty stuff from TrackerServer not to confuse Netty
                } catch (Exception e) {
                    System.err.println("Problem adding route " + uri + "\n" + e.getMessage());
//                  LOGGER.warn("One of the protocols is disabled due to port conflict");
                    if (e instanceof java.net.BindException) {
                        portFailures.put(uri, server.getPort());
//                      unregister ServerInitFactory ?
                    }
//                  e.printStackTrace();
                }
            }
//          }   // end single protocol
        }
        if (!portFailures.isEmpty()) {
            System.err.println("The following routes could not created due to 'port already in use'");
            portFailures.forEach((k, v) -> {
                System.err.println("\tport=" + v + " - uri=" + k);
            });
        }
//      System.out.println("DcsRoutesFactory DONE !");
    }

    private Map<String, BaseProtocol> getInstantiatedProtocols(String packageName) {

        /* ClassFinder could be used to instantiate ALL *Protocols at once:
         * List<Object> allBaseProtocols =
         *      ClassFinder.getInstances(packageName, "org.traccar.BaseProtocol");
         * This method was not applied since most systems do not activate ALL
         * available protocols and the instantiated Protocols with TrackerServer
         * would need to be garbage collected after method return.
         */

        Map<String, BaseProtocol> protocolList = new ConcurrentHashMap<>();
        List<Class<?>> protocolClasses =
                ClassFinder.getClasses(packageName, org.traccar.BaseProtocol.class);

        for (Class<?> protocolClass : protocolClasses) {
//          if (BaseProtocol.class.isAssignableFrom(protocolClass)) &&
            if (Context.getConfig().hasKey(BaseProtocol.nameFromClass(protocolClass) + ".port")) {
                BaseProtocol protocol = null;
                try {
                    protocol = (BaseProtocol) protocolClass.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    System.err.println(protocolClass + " could not be instantiated!");
                    e.printStackTrace();
                }
                protocolList.put(protocol.getName(), protocol);
            }
        }
        return protocolList;
    }

    /**
     * DCS Routes for ALL Protocols are directed to one output endpoint where the
     * traccar.model objects can be picked up by the system. Later a jeets.model
     * output for JPA specified Entities will be added as an alternative.
     */
    private static final class DcsRouteBuilder extends RouteBuilder {
        private final String from;
        private final String routeId;

        private DcsRouteBuilder(CamelContext context, String from, String routeId) {
            super(context);
            this.from = from;
            this.routeId = routeId;
        }

        @Override
        public void configure() throws Exception {
            from(from)
//          using the same id again, will quietly stop and replace the earlier route
            .routeId(routeId)
//          may become helpful, use outer loop to increment (keep external <key,val> references!)
//          .startupOrder(order)

//          ANALYZE
//          .inOnly("direct:traccar.model");
//          Fine tuning with seda endpoint etc.
//          .to("seda:traccar.model");
            .to("direct:traccar.model");

//          convert to jeets-pu entities, i.e.standardize
//          .to("direct:jeets.model");
        }
    }

    @Deprecated
    private Map<String, BaseProtocol> getProtocolList(String packageName) {
        Map<String, BaseProtocol> protocolList = null;
        List<String> names = null;
        try {
            names = getClassNames(packageName);
//          check empty list and null
            protocolList = getProtocolInstances(names, packageName);
//          check empty list and null
            System.out.println("loaded " + protocolList.size() + " *Protocol objects");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return protocolList;
    }

    /**
     * Map the protocol name to an instantiated BaseProtocol object. This step was
     * slightly modified from the traccar implementation, since we will start the
     * TrackerServer routes via camel-netty4. The mapping returns all information
     * and objects to create netty4: Camel routes.
     */
    @Deprecated
    private Map<String, BaseProtocol> getProtocolInstances(List<String> classNames, String packageName)
            throws Exception {

        Map<String, BaseProtocol> protocolList = new LinkedHashMap<>();
        for (String name : classNames) {
            @SuppressWarnings("rawtypes")
            Class protocolClass = Class.forName(packageName + '.' + name);
            if (BaseProtocol.class.isAssignableFrom(protocolClass)
                    && Context.getConfig().hasKey(BaseProtocol.nameFromClass(protocolClass) + ".port")) {
                BaseProtocol protocol = (BaseProtocol) protocolClass.newInstance();
                protocolList.put(protocol.getName(), protocol);
            }
        }
        return protocolList;
    }

    /**
     * The source of this code is the org.traccar.ServerManager's constructor where
     * the Protocols are loaded, configured and actvated.
     */
    @Deprecated
    private List<String> getClassNames(String packageName) throws Exception {
        List<String> names = new LinkedList<>();
        String packagePath = packageName.replace('.', '/');
        URL packageUrl = getClass().getClassLoader().getResource(packagePath);

        if (packageUrl.getProtocol().equals("jar")) {
            String jarFileName = URLDecoder.decode(packageUrl.getFile(), StandardCharsets.UTF_8.name());
            try (JarFile jf = new JarFile(jarFileName.substring(5, jarFileName.indexOf("!")))) {
                Enumeration<JarEntry> jarEntries = jf.entries();
                while (jarEntries.hasMoreElements()) {
                    String entryName = jarEntries.nextElement().getName();
                    if (entryName.startsWith(packagePath) && entryName.length() > packagePath.length() + 5) {
                        names.add(entryName.substring(packagePath.length() + 1, entryName.lastIndexOf('.')));
                    }
                }
            }
        } else {
            File folder = new File(new URI(packageUrl.toString()));
            File[] files = folder.listFiles();
            if (files != null) {
                for (File actual: files) {
                    String entryName = actual.getName();
                    names.add(entryName.substring(0, entryName.lastIndexOf('.')));
                }
            }
        }
        return names;
    }
}
