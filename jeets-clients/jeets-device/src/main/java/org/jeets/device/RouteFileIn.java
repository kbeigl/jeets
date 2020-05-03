package org.jeets.device;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class RouteFileIn extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("file://send?charset=UTF-8&preMove=.sending&move=.sent&moveFailed=.error")
        .to("direct:parse.file");
    }

}
