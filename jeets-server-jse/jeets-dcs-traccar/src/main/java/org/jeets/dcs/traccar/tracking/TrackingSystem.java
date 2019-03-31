package org.jeets.dcs.traccar.tracking;

import org.traccar.model.Position;

/**
 * This class represents the Tracking System with incoming
 * org.traccar.model.Position (and later optional
 * org.jeets.model.traccar.jpa.Device/Position/Events). Temporary for
 * development.
 * <p>
 * Should be modeled with Maven Template, i.e. moved outside of this project as
 * a customized starting point for a complete Tracking System. With/out camel
 * Main and DcsMain starters or only as jar import dependency? Developer should
 * not have to be aware of Camel at all - see hidding middle ware and maybe
 * Spring Remoting etc.
 *
 * @author kbeigl@jeets.org
 */
public class TrackingSystem {

//  Fine tuning with seda endpoint pending !!
//  internal: from("direct:traccar.model").bean(trackingSystem, "messageArrived");
    public void messageArrived(Position message) {
        System.out.println("TrackingSystem receives message " + message);
        System.out.println("Position ( id: " + message.getDeviceId() + " time: " + message.getFixTime()
        + " lat: " + message.getLatitude() + message.getLongitude() + " )");
    }
}
