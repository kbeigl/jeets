package org.jeets.dcs;

import org.apache.camel.builder.RouteBuilder;
import org.jeets.model.traccar.jpa.Device;

public class DcsRoute extends RouteBuilder { // plain Camel without Swing!

//	using internal Camel .log
//	private static final Logger LOG = LoggerFactory.getLogger(DcsRoute.class);

    public void configure() throws Exception {
    	
//    	demo in CamelTestSupport and move to higher CamelContext
//        PropertiesComponent pc = getContext().getComponent("properties", PropertiesComponent.class);
//        pc.setLocation("classpath:dcs.properties");
		log.info("configure Camel route from {}:{}?{}",	// just for testing, to be re/moved
				getContext().resolvePropertyPlaceholders("{{dcs.host}}"),
				getContext().resolvePropertyPlaceholders("{{dcs.protobuffer.port}}"),
				getContext().resolvePropertyPlaceholders("{{dcs.protobuffer.protocol}}"));

//      from("netty:tcp:{{jeets.dcs.endpoint}}&sync=true")
//		EL #{{ raises EL syntax error: Expecting expression, but creates %23 and works. Fix some time.
        from("netty:tcp://{{dcs.host}}:{{dcs.protobuffer.port}}?serverInitializerFactory=#{{dcs.protobuffer.protocol}}&sync=true")
//      Type Converter
        .convertBodyTo(Device.class)    // check exchange.getIn/Out
//      Message Translator
        .inOnly("seda:jeets-dcs?concurrentConsumers=4&waitForTaskToComplete=Never")
        .process(new AckResponder());    // only creates new Ack(789)
    }
    
}
