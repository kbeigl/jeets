package org.jeets.routes;

import org.apache.camel.builder.RouteBuilder;

/**
 * This route represents a single DCS with the ProtobufferProtocol and -Decoder.
 * It serves as a template for a higher level loop over all Protocols (and
 * ports) to create DCS routes (to be launched with spring.Main).
 * <p>
 * The DcsRoutesFactory will create routes dynamically from input parameters to
 * avoid manual creation of all routes - like this one.
 *
 * @author kbeigl@jeets.org
 */
public class ProtobufferDslRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("netty4:tcp://localhost:5200?serverInitializerFactory=#protobuffer&sync=true")
        .routeId("Protobuffer")
        .log("netty output: ${body}")
//      .to("log:org.apache.camel?level=DEBUG") < allign with log4j.props (?)
//        .process(new Processor() {
//            @Override
//            public void process(Exchange exchange) throws Exception {
//                System.out.println("from localhost:5200: " + exchange);
//                exchange.getOut().setBody(null);
////              exchange.getOut().setBody(ackBuilder.build(), Traccar.Acknowledge.class);
//            }
//        })
        .inOnly("direct:traccar.model");

        /*
         * .inOnly doesn't swallow Position, tries to send it back
         * .inOnly("seda:jeets-dcs?concurrentConsumers=4&waitForTaskToComplete=Never")
         * The problem is that traccar.*Protocols use channel.writeAndFlush(new NetworkMessage..)
         * to send responses (and acks), while the NettyConsumer also writes (again):
         * ...netty4.NettyHelper.writeBodyAsync(NettyHelper.java:109)
         * ...netty4.handlers.ServerChannelHandler.sendResponse(ServerChannelHandler.java:180)
         * which traverses the Outbound wrappers (again) ...
         * How to suppress?
         */

//        .to("direct:traccar.model");
    }

}
