package org.jeets.device;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class RouteParseFile extends RouteBuilder {

    @Override
    public void configure() throws Exception {
//      <route errorHandlerRef="redeliverEH" id="route-parse-file">

        from("direct:parse.file")
        .split().tokenize("\n").streaming()
            .to("direct:setconf.fileone")
//          .filter instead of .when !?
            .filter().simple("${header.deviceconfig} != null")
                .to("direct:device.send.hex")
                .log("response: ${body}");
    }

//    <camelContext id="camelContext-device" xmlns="http://camel.apache.org/schema/spring">
//    <errorHandler deadLetterUri="log:dead?level=ERROR"
//        id="redeliverEH" type="DeadLetterChannel">
//        <redeliveryPolicy backOffMultiplier="2"
//            maximumRedeliveries="3" retryAttemptedLogLevel="WARN" useExponentialBackOff="true"/>
//    </errorHandler>

}
