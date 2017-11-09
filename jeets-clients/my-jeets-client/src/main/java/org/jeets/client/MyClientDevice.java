package org.jeets.client;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jeets.model.traccar.jpa.Position;
import org.jeets.player.PlaybackListener;
import org.jeets.tracker.Tracker;

/**
 * Starting point for client development to interact with vehicle APIs.
 * <p>
 * Client is equipped with a jeets-gps-tracker representing a Tracking Hardware.
 * GPS motion can be simulated by using the jeets-gps-player to replay any GPS
 * trace from various internal or external resources.
 * 
 * @author kbeigl@jeets.org
 */
public class MyClientDevice implements PlaybackListener {

    private Tracker tracker;

    public MyClientDevice(Tracker tracker) {
        this.tracker = tracker;
    }

    /**
     * Player sends Positions from recorded or requested GPS traces at fixtimes.
     */
    @Override
    public void receivePositionEntity(Position positionEntity) {
        // required attribute for transmission!
        // countercheck tracker.transmitTraccarDevice (remove here?)
        positionEntity.setDevicetime(new Date());
        logger.info("Device now is at position: " + positionEntity);
        // position is sent to server as simple as
        tracker.sendPositionEntity(positionEntity);
    }

    private static Log logger = LogFactory.getLog(MyClientDevice.class);

}
