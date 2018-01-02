package org.jeets.managers;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.jeets.model.traccar.jpa.Device;
import org.jeets.model.traccar.util.Samples;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringJpaTrxTest extends CamelSpringTestSupport {

    @Test
//  currently this Test relies on postgres and persists records !
//    dao.persist can be temporarily commented to avoid this
//  create Test environment with RAM DB and INSERT records 
//  (see pu.jar or ..apache-camel-2.19.1.git\components\camel-spring\src\test\resources\org\apache\camel\spring\interceptor\springTransactionalClientDataSourceMinimalConfiguration.xml)
    public void testSpringJpaTrx() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:manager1.out")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        System.out.println("Device from 'direct:manager1.out'\n" 
                                + (Device) exchange.getIn().getBody());
                    }
                })
                .to(testEndpoint);
            }
        });

        Device device = createDevice();
//      device.setUniqueid("111");  // TODO: test unregistered

        testEndpoint.expectedMessageCount(1); 
//      testEndpoint.expectedBodiesReceived(device);

        System.out.println("create and send Device\n" + device);
        template.sendBody("direct:managers.in", device );

        testEndpoint.assertIsSatisfied();
    }
    
    static Device createDevice() {
//      creates device with id=0 ! change to @Id Long (object)?
//      return Samples.createDeviceEntity();
        return Samples.createDeviceWithTwoPositions();
//      return Samples.createDeviceWithPositionWithTwoEvents();
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
        return new ClassPathXmlApplicationContext("file:src/main/resources/META-INF/spring/spring-camel-jpa.xml");
    }

}
