package org.jeets.dcs;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.netty.ServerInitializerFactory;
import org.jeets.traccar.routing.TraccarRoute;
import org.jeets.traccar.routing.TraccarSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.traccar.Context;
import org.traccar.protocol.JeetsProtocol;
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

//  jeets-protocols-traccar -----------------------------------------

    /**
     * Register 'ruptela' ServerInitializerFactory for TCP transport.
     * <p>
     * Spring way to register the ServerInitializerFactory analog to Camel way via
     * <br>
     * <code>
     * registry.bind("ruptela", new RuptelaProtocol(null)) </code>
     */
    @Bean(name = "ruptela")
    public ServerInitializerFactory getRuptelaPipeline() {
//      traverse default.xml protocols and ports
        Class<?> protocolClass = RuptelaProtocol.class;
        return TraccarSetup.createServerInitializerFactory(protocolClass);
    }

    /* Hard coded Routes can be *created* in advance, but can only be *used* after
     * 'protocol' serverInitializerFactory is registered.       */

//  TODO meaningless name, abusing Bean to start route ..
    @Bean(name = "ruptelaXRoute") 
//  @Component !?
    public RouteBuilder createRuptelaRoute() {
//      analog: Context.getServerManager().start()
        String routeName = "ruptela";
        int port = -1; // 5046
        if (Context.getConfig().hasKey("ruptela.port")) {
            port = Context.getConfig().getInteger("ruptela.port");
            // String represents dynamic part for all protocols, see below
            // add traccar channelGroup, see Camel Netty Component: channelGroup (advanced)
            String uri = "netty:tcp://" + host + ":" + port 
                    + "?serverInitializerFactory=#" + routeName 
                    + "&sync=" + camelNettySync;
//          "&workerPool=#sharedPool&usingExecutorService=false" .. ?
            return new TraccarRoute(uri, routeName);
        }
        System.err.println("Ruptela is not configured. No Route created!");
        return null;
    }

    /* hold the protocol instance to retrieve individual server ? Register
     * 'teltonika' ServerInitializerFactory for TCP Register 'teltonika-udp'
     * ServerInitializerFactory for UDP */

    @Bean(name = "teltonika")
    public ServerInitializerFactory getTeltonikaPipeline() {
        Class<?> protocolClass = TeltonikaProtocol.class;
        return TraccarSetup.createServerInitializerFactory(protocolClass);
    }

    @Bean(name = "teltonikaXRoute") 
    public RouteBuilder createTeltonikaRoute() {
        String routeName = "teltonika";
        int port = -1; // 5027
        if (Context.getConfig().hasKey("teltonika.port")) {
            port = Context.getConfig().getInteger("teltonika.port");
            String uri = "netty:tcp://" + host + ":" + port + "?serverInitializerFactory=#" + routeName + "&sync=" + camelNettySync;
            return new TraccarRoute(uri, routeName);
        }
        System.err.println("Teltonika is not configured. No Route created!");
        return null;
    }

//  jeets-protocols with Traccar logic ------------------------------

    @Bean(name = "device") // TODO: "jeets" 
    public ServerInitializerFactory getDevicePipeline() {
        Class<?> protocolClass = JeetsProtocol.class;
        return TraccarSetup.createServerInitializerFactory(protocolClass);
    }

    @Bean(name = "protobufferXRoute")
    public RouteBuilder createProtobufferRoute() {
        String routeName = "device";  // TODO: "jeets"
        int port = -1; // 5027
        if (Context.getConfig().hasKey("device.port")) {
            port = Context.getConfig().getInteger("device.port");
            String uri = "netty:tcp://" + host + ":" + port + "?serverInitializerFactory=#" + routeName + "&sync=" + camelNettySync;
            return new TraccarRoute(uri, routeName);
        }
//      throw Exception ?
        LOGGER.error("DeviceProtocol is not configured. No Route created!");
        return null;
    }

//  Netty En/Decoders out of the box with @Component and @Service --- !!

    @Bean(name = "stringDecoder")
    public StringDecoder createStringDecoder() {
        return new StringDecoder();
    }

    @Bean(name = "stringEncoder")
    public StringEncoder createStringEncoder() {
        return new StringEncoder();
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

}
