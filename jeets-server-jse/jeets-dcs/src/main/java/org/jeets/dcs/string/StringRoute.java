package org.jeets.dcs.string;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/** @Component - automatic route discovery by Spring Boot */
@Component
public class StringRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("netty:tcp://localhost:7000?sync=true&allowDefaultCodec=false&encoders=#stringEncoder&decoders=#stringDecoder")
//      .routeId("hello").routeGroup("hello-group") // see traccar channelGroup
        .to("bean:echoService");
//      .process(new ProtobufferAck()); // create Ack(789)
    }

}
