package org.jeets.manager;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.jeets.model.traccar.jpa.Device;
import org.jeets.model.traccar.util.Samples;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ManagerTest extends CamelSpringTestSupport {
    
    private static final Logger LOG = LoggerFactory.getLogger(ManagerTest.class);

    /* Test is currently persisting to postgres !! */
    @Test
    public void testManagedRoute() throws Exception {
        String routeName = "testManagedRoute";
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:managed.end")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Device device = (Device) exchange.getIn().getBody();
                        System.out.println("Device from 'direct:managed.end'\n" + device);
                    }
                })
                .to(testEndpoint);
            }
        });
        
//      testEndpoint.expectedMessageCount(1);
//      testEndpoint.message(0).header("senddevice").isEqualTo("gts");

        Device device = createDevice();
//      device.setUniqueid("testDevice");   // set unregistered uniqueId
        System.out.println("create and send Device\n" + device);
        template.sendBody("direct:device.in", device );

//      testEndpoint.assertIsSatisfied();
        
        context.stopRoute(routeName);
        context.removeRoute(routeName);
    }
    
    private Device createDevice() {
//      return Samples.createDeviceEntity();
        return Samples.createDeviceWithTwoPositions();
//      return Samples.createDeviceWithPositionWithTwoEvents();
    }

//    @Override
//    protected RouteBuilder createRouteBuilder() throws Exception {
//        return new ManagedRoute();
//    }

    protected String testUri = "mock:result";
    protected MockEndpoint testEndpoint;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        testEndpoint = getMockEndpoint(testUri);
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
//      return new ClassPathXmlApplicationContext("spring/camel-context.xml");
        return new ClassPathXmlApplicationContext("file:src/main/resources/META-INF/spring/camel-context.xml");
    }

}
