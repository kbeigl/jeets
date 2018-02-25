Camel ActiveMQ Adapter and Component
------------------------------------

This project can be used to deploy the camel-activemq component with the WildFly Camel subsystem.

CLI scripts located within the `src/main/resources/cli` directory
automatically configure the ActiveMQ Resource Adapter. 

The project jar can be used in other projects
as ActiveMQComponent with the installed ActiveMQ Adapter.

The ActiveMQComponentProducer injects the ActiveMQConnectionFactory 
that has been configured through the ActiveMQ Resource Adapter.

A @Resource with mappedName "java:/ActiveMQConnectionFactory" is created
and @Produces a ConnectionFactory and a ActiveMQComponent @Named "activemq".

This "activemq" component can be addressed by other projects.


Prerequisites
-------------

* Maven
* A running application server with the wildfly-camel subsystem installed
* A running ActiveMQ broker

Deploy ActiveMQ Adapter
-----------------------

1. Ensure your ActiveMQ broker instance is running. 
   By default, this example expects the broker to be accessible on localhost. 
   This can be changed by editing `src/main/resources/cli/configure-resource-adapter.cli` 
   and modifying the `ServerUrl` attribute from `tcp://127.0.0.1:61616` 
   to your desired host name or IP address
   
2. Start the application server in standalone mode 
   `${JBOSS_HOME}/bin/standalone.sh -c standalone-full-camel.xml`
   
3. `mvn install -Pdeploy-amq-rar` create jar AND deploy the ActiveMQ resource adapter rar
   If running Maven in Eclipse you have to explicitly set and unset the Maven Profile.

4. Restart the application server for the resource adapter configuration to take effect!

Undeploy ActiveMQ Adapter
-------------------------

To undeploy the adapter run `mvn clean -Pdeploy-amq-rar`.

This step removes the ActiveMQ resource adapter configuration 
but this will not take effect until the application server has been restarted!

Note that the adapter can be installed once and for all and 
you don't have to remove the adapter unless you have an explicit reason.
 
Install ActiveMQComponent
-------------------------

Use `mvn install` to create the project jar with the ActiveMQComponentProducer

This jar is not deployed and should be imported by higher level projects.

