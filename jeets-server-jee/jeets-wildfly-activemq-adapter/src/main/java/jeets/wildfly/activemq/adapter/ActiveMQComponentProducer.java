package jeets.wildfly.activemq.adapter;

import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.jms.ConnectionFactory;

import org.apache.activemq.camel.component.ActiveMQComponent;

public class ActiveMQComponentProducer {

    /**
     * Inject the ActiveMQConnectionFactory that has been configured through the ActiveMQ Resource Adapter
     */
    @Resource(mappedName = "java:/ActiveMQConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Produces
    @Named("activemq")  // jeetsmq
    public ActiveMQComponent createActiveMQComponent() {
        ActiveMQComponent activeMQComponent = ActiveMQComponent.activeMQComponent();
//      these don't work here
//      System.setProperty("org.apache.activemq.SERIALIZABLE_PACKAGES", "*");
//      activeMQComponent.setTrustAllPackages(true);
//      -> add property to WF start script
//      -Dorg.apache.activemq.SERIALIZABLE_PACKAGES="*" 
        activeMQComponent.setConnectionFactory(connectionFactory);
        return activeMQComponent;
    }
}
