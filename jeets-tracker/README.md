JeeTS Tracker
=============
The JeeTS Tracker is a standalone JSE application and can be launched via command line. The JeeTS repository also provides a small server application (..) which can also be launched via command line to display the messages.

The Tracker is based on 
- the JeeTS Protocols package (..), 
  which implies the Protobuffer libraries and handling.
- the JeeTS Hibernate Persistence package,
  which implies the Database Driver, ORM Entities and Hibernate libraries.

  !!! These dataformats have to be alligned !!!

Tracker application
===================
The tracker..jar represents a tracker hardware and can be 'attached' 
to any RealObject to be tracked. Protocols and Database can be customized 
to a project's needs and the operation mode can be set via properties or getters.

Standalone Instructions
=======================
start java -jar server.jar ..
 ..
start java -jar tracker.jar > immediately sends Positions and shuts down
 ..
