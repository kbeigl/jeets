package org.jeets.dcs.traccar;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.netty.ServerInitializerFactory;
import org.apache.camel.spi.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.traccar.BaseProtocol;
import org.traccar.Context;
import org.traccar.TrackerServer;
import org.traccar.protocol.RuptelaProtocol;
import org.traccar.protocol.TeltonikaProtocol;

import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Currently all available protocols are registered with a name to be applied in
 * Camel routes.
 * <p>
 * The registration should not take too much resources (for the time being) and
 * does not imply an instantiated Camel route. Later the hard coded registration
 * should be replaced with a dynamic registration (via a BaseClass?) according
 * to a configuration file.
 * <p>
 * A similar dynamic solution should be created for the TypeConverters hard
 * coded in /resources/META-INF/services/org/apache/camel/TypeConverter
 *
 * @author kbeigl@jeets.org
 */
@Configuration // source of bean definitions for the application context
public class Config {

    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);

    @Bean(name = "stringDecoder")
    public StringDecoder createStringDecoder() {
        return new StringDecoder();
    }

    @Bean(name = "stringEncoder")
    public StringEncoder createStringEncoder() {
        return new StringEncoder();
    }

//  ********* see DcsRoutesFactory to be replaced *********

//  currently we are relying on the listed order of Beans ..

    /** Register 'ruptela' ServerInitializerFactory for TCP transport. */
    @Bean(name = "ruptela")
    public ServerInitializerFactory getRuptelaPipeline() {
//      traverse default.xml protocols and ports
        Class<?> protocolClass = RuptelaProtocol.class;
        return createServerInitializerFactory(protocolClass);
    }

    /** Register 'teltonika' ServerInitializerFactory for TCP transport. */
    @Bean(name = "teltonika")
    public ServerInitializerFactory getTeltonikaPipeline() {
        Class<?> protocolClass = TeltonikaProtocol.class;
        return createServerInitializerFactory(protocolClass);
    }

    /**
     * The from endpoint for each protocol must be set to false!
     * <p>
     * The Traccar Pipeline and -Decoders are implemented WITH ACK response, i.e.
     * channel.writeAndFlush. Therefore the Camel endpoint, i.e. NettyConsumer,
     * should NOT return a (addtional) response. This behavior should be observed ..
     * <br>
     * Note that this boolean variable is attached to the URI as String 'true' /
     * 'false'. Maybe apply String for type safety.
     */
    private boolean camelNettySync = false;
    /**
     * camel-netty and/or spring are/is tedious about localhost, which doesn't
     * accept external access (in ubuntu). On the remote system 0.0.0.0 should be
     * used instead of 127.0.0.1.
     */
    private String host = "0.0.0.0";

    /*
     * Hard coded Routes can only be used after 'ruptela' serverInitializerFactory
     * is registered.
     */

//  TODO meaningless name, missusing Bean to start route ..
    @Bean(name = "ruptelaXRoute") 
//  @Component !?
    public RouteBuilder createRuptelaRoute() {
        String routeName = "ruptela";
        int port = -1; // 5046
        if (Context.getConfig().hasKey("ruptela.port")) {
            port = Context.getConfig().getInteger("ruptela.port");
            // String represents dynamic part for all protocols, see below
            // add traccar channelGroup, see Camel Netty Component: channelGroup (advanced)
            String uri = "netty4:tcp://" + host + ":" + port + "?serverInitializerFactory=#" + routeName + "&sync=" + camelNettySync;
            return new DcsRouteBuilder(uri, routeName);
        }
        System.err.println("Ruptela is not configured. No Route created!");
        return null;
    }

//  hold the protocol instance to retrieve individual server ?
//    /** Register 'teltonika' ServerInitializerFactory for TCP transport. */
//    @Bean(name = "teltonika")
//    /** Register 'teltonika-udp' ServerInitializerFactory for UDP transport. */
//    @Bean(name = "teltonika")

    @Bean(name = "teltonikaXRoute") 
    public RouteBuilder createTeltonikaRoute() {
        String routeName = "teltonika";
        int port = -1; // 5027
        if (Context.getConfig().hasKey("teltonika.port")) {
            port = Context.getConfig().getInteger("teltonika.port");
            // String represents dynamic part for all protocols, see below
            // add traccar channelGroup, see Camel Netty Component: channelGroup (advanced)
            String uri = "netty4:tcp://" + host + ":" + port + "?serverInitializerFactory=#" + routeName + "&sync=" + camelNettySync;
            return new DcsRouteBuilder(uri, routeName);
        }
        System.err.println("Teltonika is not configured. No Route created!");
        return null;
    }

    /**
     * Currently only creating "tcp" servers
     */
    private ServerInitializerFactory createServerInitializerFactory(Class<?> protocolClass) {
        contextInit(); // as needed
        // could do without:
        String protocolName = BaseProtocol.nameFromClass(protocolClass);
        BaseProtocol protocol = instantiateProtocol(protocolClass);
        String transport = "tcp";
        TrackerServer server = getProtocolServer(transport, protocol);
        // compose URI and attach to server.setCamelUri() !
        // server can be null !
        if (server == null) {
            LOGGER.warn("No server found for '" + transport + ":" + protocolName);
            return null;
        }
        return server.getServerInitializerFactory();
    }

    /**
     * Pick udp or tcp server, if present.
     */
    private TrackerServer getProtocolServer(String transport, BaseProtocol protocol) {
        for (TrackerServer server : protocol.getServerList()) {
            if (transport.equals("udp") && server.isDatagram()) {
                return server;
            } else if (transport.equals("tcp") && !server.isDatagram()) {
                return server;
            }
        }
        return null;
    }

//  TODO see DcsRoutesFactory
    private void createTraccarDcsRoutes(CamelContext camelContext, Registry registry) {
/*
        StringBuffer uri = new StringBuffer("netty4:" + (server.isDatagram() ? "udp" : "tcp") + "://");
        uri.append((server.getAddress() == null ? "localhost" : server.getAddress()) + ":" + server.getPort() + "?");
        String serverInitializerFactory = server.isDatagram() ? protocolName + "-udp": protocolName;
        uri.append("serverInitializerFactory=#" + serverInitializerFactory);
        uri.append("&sync=true");
//      "&workerPool=#sharedPool&usingExecutorService=false" .. ?
        String camelUri = uri.toString();
        System.out.println("Camel URI: " + camelUri);

        System.out.println("create Route '" + serverInitializerFactory + "' from(\"" + uri + "\") ...");
???     CamelContext context = new DefaultCamelContext();
        try {
            camelContext.addRoutes(new DcsRouteBuilder(camelContext, uri, serverInitializerFactory));
//          add route counter in addition to: protocolList.size() + " *Protocol objects"
//          may need to 'clean' Netty stuff from TrackerServer not to confuse Netty
        } catch (Exception e) {
            System.err.println("Problem adding route " + uri + "\n" + e.getMessage());
//          LOGGER.warn("One of the protocols is disabled due to port conflict");
            if (e instanceof java.net.BindException) {
                portFailures.put(uri, server.getPort());
//              unregister ServerInitFactory ?
            }
//          e.printStackTrace();
        }
 */
    }

    /**
     * DCS Routes for ALL Protocols are directed to one output endpoint where the
     * traccar.model objects can be picked up by the system.
     * <p>
     * Re-using the same routeId, will quietly stop and replace the earlier route.
     */
//  SpringRouteBuilder !?
    private static final class DcsRouteBuilder extends RouteBuilder {
        private final String from;
        private final String routeId;

        private DcsRouteBuilder(String from, String routeId) {
            this.from = from;
            this.routeId = routeId;
        }

        /**
         * At this point the serverInitializerFactory is configured with it's
         * &lt;name&gt; only as meaningless String. Only before the Route is
         * instantiated by a first message the serverInitializerFactory has to be
         * registered.
         */
        @Override
        public void configure() throws Exception {
            from(from)
            .routeId(routeId)
//          .routeGroup("hello-group")
//          .startupOrder(order)
            .log("DCS ${body.protocol} output: position ( time: ${body.deviceTime} "
                    + "lat: ${body.latitude} lon: ${body.longitude} )")

//          Fine tuning with seda endpoint etc.
//          .to("seda:traccar.model");
//          jeets-dcs:traccar.model  ;)
            .to("direct:traccar.model");  
//          already set in sync=false (?):
//          .setExchangePattern(ExchangePattern.InOnly)
//          .to("seda:jeets-dcs?concurrentConsumers=4&waitForTaskToComplete=Never")
//          .inOnly("seda:jeets-dcs?concurrentConsumers=4&waitForTaskToComplete=Never")
        }
    }

    /**
     * Each BaseProtocol instance provides one or two TrackerServer instances for
     * tcp and/or udp transport. Each server holds a configured BasePipelineFactory
     * which is used to get a ServerInitializerFactory and register the instance for
     * the camel-netty life cycle.
     */
    private BaseProtocol instantiateProtocol(Class<?> protocolClass) {
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
     * Initialize traccar.Context only once. Method returns fast, if context is
     * initialized, i.e. NullPointerException does not occur.
     */
    @SuppressWarnings("deprecation")
    private void contextInit() {
        try {
            Context.getConfig().getString("event.enable");
        } catch (NullPointerException npe) {
//          Context is not initialized yet, do now
            try {
//              TODO remove hard coded path
                Context.init("./setup/traccar.xml");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
