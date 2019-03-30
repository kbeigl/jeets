


The jeets-dcs-manager can bind different protocol decoders to individual ports.
Each protocol will extract client messages to database entities for the Traccar database.
The entities can be retrieved in two flavors: The original traccar Position and Event
or the jeets Device and Position specified by JPA for handling with an EntityManager.

Generally the jeets-dcs-manager can be compared to Traccar's ServerManager().
Both manage the Device Communication Servers for various protocols


What the jeets-dcs-manager is not
--
The jeets-dcs-manager can manage all protocols defined in the Traccar GTS,
but is far from being a GTS. Actually a GTS begins its work after receiving
the System Entities from one or many DCS. Therefore this project can be used
if you want to break out of the Traccar System and create your own Tracking logic
or feed live information into a proprietary system.

Traccar integration steps
--
Since the tracker-server.jar generated from the Traccar sources
does not include the configuration files default.xml and traccar.xml
we need to supply them separately.

Note that the DCS manager includes dedicated org.jeets.traccar packages
in order to integrate the original tracker-server.jar without any changes.
The jar actually represents the protocol plugin and besides DCS related
classes the majority of classes is ignored. 



Build
--

To build this project use

    mvn install

To run this project with Maven use

    mvn camel:run

For more help see 

    http://jeets.org/

