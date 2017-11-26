package org.jeets.georouter;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;

public class Main {
    
//  TODO: add switch between vm and tcp to args on command line

    public static void main(String args[]) throws Exception {

        SimpleRegistry registry = new SimpleRegistry();
        CamelContext context = new DefaultCamelContext(registry);
        ActiveMQComponent activemq = getAmqComponent(vmTransport);
        context.addComponent("activemq", activemq);
        
//      include GeoRouteR
//      context.addRoutes(new GeoRouteS());

//        context.start();
    }
    
    /**
     * Use this to manually switch from vm: (true) to tcp: (false)
     * or override individual getConnectionFactory(..) methods.
     */
    static boolean vmTransport = true;

    /**
     * Test with embedded VM- or TCP- transport.
     * <p>
     * For TCP testing the external ActiveMQ has to be running. <br>
     * For Maven runs the vm: transport should be activated to avoid project
     * external dependencies. <br>
     * Developers can manually switch to the external ActiveMQ.
     * 
     * @param vmTransport - true=vm false=tcp
     */
    public static ActiveMQConnectionFactory getConnectionFactory(boolean vmTransport) {
        ActiveMQConnectionFactory amqFactory;
        if (vmTransport) {
            amqFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        }
        else { // tcp://localhost:61616
            amqFactory = new ActiveMQConnectionFactory(
                    ActiveMQConnection.DEFAULT_USER,
                    ActiveMQConnection.DEFAULT_PASSWORD, 
                    ActiveMQConnection.DEFAULT_BROKER_URL);
        }
        amqFactory.setTrustAllPackages(true);
//      these don't work (also try in xml:
//      List<String> trustedPersistenceUnit = new ArrayList<String>();
//      trustedPersistenceUnit.add("org.jeets.model.traccar.jpa");
//      amqFactory.setTrustedPackages(trustedPersistenceUnit);
//      amqFactory.setTrustedPackages(Arrays.asList("org.jeets.model.traccar.jpa"));
        return amqFactory;
    }
    
    public static ActiveMQComponent getAmqComponent(boolean vmTransport) {
        ActiveMQConnectionFactory activeMqConnectionFactory = 
                Main.getConnectionFactory(vmTransport);
        ActiveMQComponent activeMQComponent = new ActiveMQComponent();
        activeMQComponent.setConnectionFactory(activeMqConnectionFactory);
//        ConnectionFactory connectionFactory = activeMqConnectionFactory;
//        activeMQComponent.setConnectionFactory(connectionFactory);
        return activeMQComponent;
    }


    
    
}
