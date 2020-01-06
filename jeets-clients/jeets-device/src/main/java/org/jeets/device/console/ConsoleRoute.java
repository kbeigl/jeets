package org.jeets.device.console;

import org.apache.camel.builder.RouteBuilder;

/**
 * Route a HEX String from keyboard to device in order to send the message to a
 * server, receive an ACK and display it to system.out
 */
public class ConsoleRoute extends RouteBuilder {
//  TODO different route with "stream:fileName ..."

    @Override
    public void configure() throws Exception {
        from("stream:in?promptMessage=send HEX: ")
        .to("bean:device?method=sendHexMessage")
        .transform(simple("${body.toUpperCase()}")) // optional
//      add duration [ms] (to logger?)
//      .to("stream:out")    // optional System.out.println
        .log("received HEX: ${body}")
        ;
    }

}
