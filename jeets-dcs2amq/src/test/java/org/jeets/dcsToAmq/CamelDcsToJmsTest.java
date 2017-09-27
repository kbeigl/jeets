package org.jeets.dcsToAmq;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.jeets.dcsToAmq.DcsToAmqRoute;
import org.jeets.dcsToAmq.steps.DeviceProtoExtractor;
import org.jeets.model.traccar.jpa.Device;
import org.jeets.model.traccar.util.Samples;
import org.jeets.protocol.Traccar;
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
     * Use this to manually switch from vm: (true) to tcp: (false)
     * or override individual getConnectionFactory(..) methods.
     */
    private static boolean activeMqVmTransport = true;
    
    /* TODO camel-example-spring-jms:
     * 1. replace broker and camel config with camel-server.xml
     * 2. implement spring remoting on different VM (?)
     */

    /**
     * Most simple test case to send a Device Entity to an 'in' queue and
     * forward it to an 'out' queue.
     */
    @Test
    public void testDeviceEntityToJms() throws Exception {
//      CamelContext provided as 'context' member by CamelTestSupport

        ActiveMQConnectionFactory activeMqConnectionFactory = getConnectionFactory(activeMqVmTransport);

//      cast to JMS spec
        ConnectionFactory connectionFactory = activeMqConnectionFactory;

        // explicit 'test-' component name
        context.addComponent("test-jms", 
                JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

        LOG.info("create and add DcsRoute ...");
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("test-jms:queue:device.in")
                .to("test-jms:queue:device.out");
            }
        });

        ProducerTemplate template = context.createProducerTemplate();
        context.start();
        template.sendBody("test-jms:queue:device.in", 
                Samples.createDeviceEntity());

        Thread.sleep(1000);
        context.stop();
    }

    /**
     * Test actual DcsToActiveMqRoute from netty tcp, via transformation from
     * proto to entity to MQ with three registered classes (see overridden
     * createRegistry method). Uses 'activemq' instead of 'jms' component.
     */
    @Test
    public void testDcsToActiveMqRoute() throws Exception {
        LOG.info("create and add DcsRoute ...");
        context.addRoutes(new DcsToAmqRoute() );
        context.addRoutes(new RouteBuilder() {
            public void configure() throws Exception {
                from("activemq:queue:device.in?connectionFactory=#activeMqConnectionFactory")
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
        MockEndpoint mock = getMockEndpoint("mock:result");
        Traccar.Acknowledge response = (Traccar.Acknowledge) template   // Traccar.Device
                .requestBody("netty4:tcp://localhost:5200?clientInitializerFactory=#ack&sync=true", 
                        org.jeets.protocol.util.Samples.createDeviceWithPositionWithOneEvent());

        LOG.info("client received response: " + response);              //     jpa.Device
        assertEquals(789, response.getDeviceid());
        
        mock.expectedMessageCount(1);
        assertMockEndpointsSatisfied();

        Exchange message = mock.getExchanges().get(0);
        Device device = (Device) message.getIn().getBody();
        assertEquals(1, device.getPositions().size());
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        registry.bind("device", new DeviceProtoExtractor(null));    // request  to server
        registry.bind("ack", new AckProtoExtractor(null));          // response to client
        
        ActiveMQConnectionFactory activeMqConnectionFactory = 
                org.jeets.dcsToAmq.CamelDcsToJmsTest.getConnectionFactory(activeMqVmTransport);

        registry.bind("activeMqConnectionFactory", activeMqConnectionFactory);
//      cast to JMS spec
//      ConnectionFactory connectionFactory = activeMqConnectionFactory;
//      registry.bind("mqConnectionFactory", connectionFactory);

        return registry;
    }

    /**
     * Test with embedded VM- or TCP- transport.
     * <p>
     * For TCP testing the external ActiveMQ has to be running. <br>
     * For Maven runs the vm: transport should be activated to avoid project
     * external dependencies. <br>
     * Developers can manually switch to the external ActiveMQ.
     * 
     * @param activeMqVmTransport - true=vm false=tcp
     */
    public static ActiveMQConnectionFactory getConnectionFactory(boolean activeMqVmTransport) {
        ActiveMQConnectionFactory amqFactory;
        if (activeMqVmTransport) {
            amqFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        }
        else { // tcp://localhost:61616
            amqFactory = new ActiveMQConnectionFactory(
                    ActiveMQConnection.DEFAULT_USER,
                    ActiveMQConnection.DEFAULT_PASSWORD, 
                    ActiveMQConnection.DEFAULT_BROKER_URL);
        }
        amqFactory.setTrustAllPackages(true);
//      these don't work:
//      List<String> trustedPersistenceUnit = new ArrayList<String>();
//      trustedPersistenceUnit.add("org.jeets.model.traccar.jpa");
//      amqFactory.setTrustedPackages(trustedPersistenceUnit);
//      amqFactory.setTrustedPackages(Arrays.asList("org.jeets.model.traccar.jpa"));
        return amqFactory;
    }

}
