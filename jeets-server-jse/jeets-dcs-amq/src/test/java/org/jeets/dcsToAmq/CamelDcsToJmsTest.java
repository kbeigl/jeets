package org.jeets.dcsToAmq;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.mock.MockEndpoint;
//import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.spi.Registry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.jeets.dcsToAmq.DcsToAmqRoute;
import org.jeets.dcsToAmq.steps.DeviceProtoExtractor;
import org.jeets.model.traccar.jpa.Device;
import org.jeets.model.traccar.util.Samples;
import org.jeets.protocol.Traccar;
import org.jeets.protocol.Traccar.Device.Builder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Plain Camel (no Spring, no xml) tests to 'jms' Component
 * 
 * @author kbeigl@jeets.org
 */
public class CamelDcsToJmsTest extends CamelTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(CamelDcsToJmsTest.class);

    /**
     * Most simple test case to send a Device Entity to an 'in' queue and
     * forward it to an 'out' queue.
     */
    @Test
    public void testDeviceToJms() throws Exception {
        
        bindBeans();

        // cast to JMS spec
        ConnectionFactory connectionFactory = activeMqConnectionFactory;

        // CamelContext is provided as 'context' member instance by
        // CamelTestSupport. Explicit 'test-' component name
        context.addComponent("test-jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("test-jms:queue:device.in")
                .to("test-jms:queue:device.out");
            }
        });

        context.start();
        template.sendBody("test-jms:queue:device.in", Samples.createDeviceEntity());

        Thread.sleep(1000);
//      template.stop();
        context.stop();
    }

    /**
     * Test actual DcsToActiveMqRoute from netty tcp, via transformation from
     * proto to entity to MQ with three registered classes (see overridden
     * createRegistry method). Uses 'activemq' instead of 'jms' component.
     */
    @Test
    public void testDeviceFromNettyToAmqToMock() throws Exception {
        LOG.info("create and add DcsToAmqRoute ...");

//      temporarily for Camel 2 backward compatibility
//      see https://camel.apache.org/components/latest/file-component.html
//      From Camel 3 onwards ...
//        context.setLoadTypeConverters(true);

        bindBeans();

        context.addRoutes(new DcsToAmqRoute() );
        context.addRoutes(new RouteBuilder() {
            public void configure() throws Exception {
                from("activemq:queue:hvv.in?connectionFactory=#activeMqConnectionFactory")
//              from("activemq:queue:device.in?connectionFactory=#activeMqConnectionFactory")
//              optional process for logging only
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        Device device = (Device) exchange.getIn().getBody();
                        LOG.info("received jpa.Device {} with {} positions.", 
                                device.getUniqueid(), device.getPositions().size());
                    }
                })
                .to("mock:result");
            }
        });

//      now send message from client
        MockEndpoint mock = getMockEndpoint("mock:result");
        Builder devPosEv = org.jeets.protocol.util.Samples.createDeviceWithPositionWithOneEvent();
        Traccar.Acknowledge response = (Traccar.Acknowledge) template   // Traccar.Device
                .requestBody("netty:tcp://localhost:5200?clientInitializerFactory=#ack&sync=true", 
                        devPosEv);

        LOG.info("client received response: " + response);              //     jpa.Device
        assertEquals(789, response.getDeviceid());
        
        mock.expectedMessageCount(1);
        assertMockEndpointsSatisfied();

        Exchange message = mock.getExchanges().get(0);
        Device device = (Device) message.getIn().getBody();
        assertEquals(1, device.getPositions().size());
    }

    ActiveMQConnectionFactory activeMqConnectionFactory;

    protected CamelContext createCamelContext() throws Exception {  
        CamelContext context = super.createCamelContext();  

        System.out.println("createCamelContext");

//      temporarily for Camel 2 backward compatibility
//      see https://camel.apache.org/components/latest/file-component.html
//      From Camel 3 onwards ...
        context.setLoadTypeConverters(true);
        
        return context;
    }
    
    /*
     * createRegistry() from camel-test is deprecated. Use createCamelRegistry if
     * you want to control which registry to use, however if you need to bind beans
     * to the registry then this is possible already with the bind method on
     * registry, and there is no need to override this method.
     */
//    @Override
//    protected Registry createCamelRegistry() throws Exception {
//        Registry registry = super.createCamelRegistry();
    
    private void bindBeans() {

        LOG.info("bindBeans on " + context);

//        context.setLoadTypeConverters(true);

//      settings from Main.main
        context.getRegistry().bind("device", new DeviceProtoExtractor(null));    // request  to server
//        registry.bind("device", new DeviceProtoExtractor(null));    // request  to server
//      use Vm Transport for testing
        activeMqConnectionFactory = Main.getConnectionFactory(true);
        context.getRegistry().bind("activeMqConnectionFactory", activeMqConnectionFactory);
//        registry.bind("activeMqConnectionFactory", activeMqConnectionFactory);

//      cast to JMS spec
//      ConnectionFactory connectionFactory = activeMqConnectionFactory;
//      registry.bind("mqConnectionFactory", connectionFactory);

        // for response test
        context.getRegistry().bind("ack", new AckProtoExtractor(null));           
//        registry.bind("ack", new AckProtoExtractor(null));           

//        return registry;
//        return context.getRegistry();
    }

}
