The JeeTS Tracker
=================
The JeeTS Tracker Jar is a standalone JSE application and can be launched via command line.  
It can be used to send messages to a host:port for integration testing client and server software.

The JeeTS Tracker Class can be added to your client tracking app as a Tracker Component  
to take care of sending messages until they are acknowledged from the Tracking System.

The Tracker includes the jeets-protocols to send Protobuffer messages 
aligned with the Traccar GTS Model, i.e. Database.

The JeeTS Protocols package implies the Protobuffer libraries and handling,
Hibernate with Database Driver, ORM Entities and libraries.

Tracker application
===================
The Tracker represents a tracker hardware and can be 'attached' to any RealObject to be tracked.  
Protocols and Database can be customized to a project's needs and the operation mode can be set via properties or getters.

Standalone Instructions
=======================
The jeets-tracker.jar (jeets-tracker-1.1-RC-jar-with-dependencies.jar) is created with 'mvn package'.  
The jar is automatically equipped with a tracker.properties file with variables from the repo's main jeets.properties file.  
To adjust the properties to your needs you can edit this file  
or you can create your own mycompany.jeets.properties prod.jeets.properties dev.jeets.properties etc.

After creating the jeets-tracker.jar enter this command line (adjust the directory) 
without arguments or with '-h' ..

    java -jar target\jeets-tracker-1.1-RC-jar-with-dependencies.jar [-h]

.. to display the usage screen:

    usage of jeets-tracker.jar
    <no args>       - this help screen
    -h              - this help screen
    -props          - use default tracker.properties (in jar) to
                      send sample Protobuffer Messages
    host port       - send sample Protobuffer Messages
    host port "messageString" - send any String message (for testing)

At development time inside the repository structure you can also launch via Maven with:

    mvn exec:java -Dexec.mainClass="org.jeets.tracker.Main" -Dexec.args="'127.0.0.1' 5200"




start java -jar tracker.jar > immediately sends Positions and shuts down
 ..

 
 
 
 
 
 
 