package org.jeets.dcs.traccar;

import org.traccar.model.Position;

/**
 * This class represents the Tracking System with incoming
 * org.traccar.model.Position entities.
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
