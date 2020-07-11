package org.jeets.dcs.init.routes;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.Registry;
import org.jeets.util.ClassFinder;
import org.traccar.BaseProtocol;
import org.traccar.Context;
import org.traccar.TrackerServer;

public class DcsRoutesFactory {
    private final String host = "localhost";

    public void createTraccarDcsRoutes(CamelContext camelContext, Registry registry) {

        Map<String, BaseProtocol> protocolList = getInstantiatedProtocols("org.traccar.protocol");

        System.out.println("instantiated " + protocolList.size() + " *Protocol objects");
        Map<String, Integer> portFailures = new ConcurrentHashMap<>();

        for (String protocolName: protocolList.keySet()) {
            String serverInitializerName = protocolName; // "teltonika"

            BaseProtocol protocolObject = protocolList.get(protocolName);
            for (TrackerServer server : protocolObject.getServerList()) {
                String transport = server.isDatagram() ? "udp" : "tcp";
                String uri = "netty:" + transport + "://";
                String serverHost = (server.getAddress() == null) ? 
                        host : server.getAddress();
                uri += serverHost + ":" + server.getPort() + "?";
                serverInitializerName = (transport.equals("tcp")) ? 
                        protocolName : protocolName + "-" + transport;
                uri += "serverInitializerFactory=#" + serverInitializerName + "&sync=true";

                System.out.println("register: " + serverInitializerName + " => " + protocolObject);
                camelContext.getRegistry().bind(serverInitializerName, server.getServerInitializerFactory());

                System.out.println("create Route '" + serverInitializerName + "' from(\"" + uri + "\") ...");
                try {
                    camelContext.addRoutes(new DcsRouteBuilder(camelContext, uri, serverInitializerName));
                } catch (Exception e) {
                    System.err.println("Problem adding route " + uri + "\n" + e.getMessage());
                    if (e instanceof java.net.BindException) {
                        portFailures.put(uri, server.getPort());
                    }
//                  e.printStackTrace();
                }
            }
        }

        if (!portFailures.isEmpty()) {
            System.err.println("The following routes could not created due to 'port already in use'");
            portFailures.forEach((k, v) -> {
                System.err.println("\tport=" + v + " - uri=" + k);
            });
        }
    }

    @Deprecated
    private Map<String, BaseProtocol> getInstantiatedProtocols(String packageName) {

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

    @Deprecated
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
            .routeId(routeId)
            .to("direct:traccar.model");
        }
    }

}
