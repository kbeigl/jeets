package org.jeets.georouter;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;

/**
 * plain Camel start without xml 
 */
public class Main {
    
//  TODO: add switch between vm and tcp to args on command line

    public static void main(String args[]) throws Exception {
//      JndiRegistry registry = new JndiRegistry();
//      registry.bind( not .put );
        ActiveMQConnectionFactory activeMqConnectionFactory = 
                getConnectionFactory(activeMqVmTransport);
        SimpleRegistry registry = new SimpleRegistry();
        registry.put("activeMqConnectionFactory", activeMqConnectionFactory);
        CamelContext context = new DefaultCamelContext(registry);
        
//      or see AmQTileMapperTest
//      camelContext.addComponent("activemq", activeMQComponent);

//      include GeoRouteR
//      context.addRoutes(new GeoRouteS());

        context.start();
    }
    
    /**
     * Use this to manually switch from vm: (true) to tcp: (false)
     * or override individual getConnectionFactory(..) methods.
     */
    static boolean activeMqVmTransport = true;

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
//      these don't work (also try in xml:
//      List<String> trustedPersistenceUnit = new ArrayList<String>();
//      trustedPersistenceUnit.add("org.jeets.model.traccar.jpa");
//      amqFactory.setTrustedPackages(trustedPersistenceUnit);
//      amqFactory.setTrustedPackages(Arrays.asList("org.jeets.model.traccar.jpa"));
        return amqFactory;
    }

}
