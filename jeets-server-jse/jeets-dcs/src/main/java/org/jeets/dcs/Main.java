package org.jeets.dcs;

import org.apache.camel.CamelContext;
import org.apache.camel.component.netty4.ChannelHandlerFactories;
import org.apache.camel.component.netty4.ChannelHandlerFactory;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.jeets.dcs.steps.DeviceProtoExtractor;

public class Main {

    public static void main(String args[]) throws Exception {
        
        SimpleRegistry registry = new SimpleRegistry();
//      to be tested
//        ChannelHandlerFactory decoder =
//                ChannelHandlerFactories.newLengthFieldBasedFrameDecoder(2048, 0, 4, 0, 4);
//        registry.put("decoder", decoder);

        registry.put("device", new DeviceProtoExtractor(null));
        CamelContext context = new DefaultCamelContext(registry);
//        context.addComponent("jeets-dcs", new DcsComponent());    // auto discovered
        context.addRoutes(new DcsRoute() );
        context.start();

    }
    
}
