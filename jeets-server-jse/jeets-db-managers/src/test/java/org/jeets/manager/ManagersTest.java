package org.jeets.manager;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.jeets.model.traccar.jpa.Device;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ManagersTest extends CamelSpringTestSupport {

//    @Test
    public void testManagedRoute() throws Exception {

        String routeName = "testManagedRoute";
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:manager1.out")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Device device = (Device) exchange.getIn().getBody();
                        System.out.println("Device from 'direct:manager1.out'\n" + device);
                    }
                })
                .to(testEndpoint);
            }
        });

        testEndpoint.expectedMessageCount(1); 

        Device device = ManagerTest.createDevice();
        System.out.println("create and send Device\n" + device);
        template.sendBody("direct:managers.in", device );

        testEndpoint.assertIsSatisfied();
        
    }
    
    protected String testUri = "mock:result";
    protected MockEndpoint testEndpoint;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        testEndpoint = getMockEndpoint(testUri);
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("file:src/main/resources/META-INF/spring/camel-context.xml");
    }

}
