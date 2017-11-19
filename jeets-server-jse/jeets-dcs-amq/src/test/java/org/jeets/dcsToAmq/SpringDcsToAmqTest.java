package org.jeets.dcsToAmq;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringDcsToAmqTest extends CamelTestSupport {

    // TODO move inside individual tests with individual xml's
    ConfigurableApplicationContext appContext = new ClassPathXmlApplicationContext("activemq.xml");

    @Test
    public void testDcsToActiveMqRoute() throws Exception {
        // JndiContext jndiContext = new JndiContext();
        // jndiContext.bind("testBean", new TestBean());
        // CamelContext camelContext = new DefaultCamelContext(jndiContext);

        context.addComponent("activemq", ActiveMQComponent
                .activeMQComponent("vm://localhost?broker.persistent=false"));
//        System.setProperty("org.apache.activemq.SERIALIZABLE_PACKAGES","*");
        
        try {
            context.addRoutes(new RouteBuilder() {
                public void configure() {
                    from("activemq:queue:start")
//                  .to("bean:testBean?method=hello")
                    .log("${body}")
                    .to("mock:result");
                }
            });

            MockEndpoint mock = getMockEndpoint("mock:result");
//          ProducerTemplate template = context.createProducerTemplate();
//          JmsMessagingTemplate jmsTemplate = ..;

            context.start();
            int count = 5;
            for (int i = 0; i < count; i++) {
//              jmsTemplate.send(Samples.createDeviceEntity());
//              template.sendBody("activemq:queue:start", Samples.createDeviceEntity());
                template.sendBody("activemq:queue:start", "hello" + i);
            }
            Thread.sleep(1000);
            
            mock.expectedMessageCount(count);
            assertMockEndpointsSatisfied();

//            Exchange message = mock.getExchanges().get(0);
//            Device device = (Device) message.getIn().getBody();
//            assertEquals(1, device.getPositions().size());

        } finally {
            context.stop();
        }
    }

}
