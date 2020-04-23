package org.jeets.georouter;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.SimpleRegistry;

public class Main {
    
//  TODO: add switch between vm and tcp to args on command line

// 10.12.17 HVV Polygon for all U- and S-Bahn 
// north of Elbe! only S3/31 Harburg missing ?
/* stored in Traccar
"POLYGON((53.56792140775653 9.68520794544665, 53.583192726853184 9.704520114713967, 53.65874543119108 9.792285254115408, 53.66896759260419 9.862065603846874, 53.713234979220914 9.98703508626874, 53.70617361494871 10.143676092738454, 53.66801373097874 10.312425305210809, 53.49725718018698 10.38011291321167, 53.48042049613247 10.194708457494505, 53.56792140775653 9.68520794544665))"
*/


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
