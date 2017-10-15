package org.jeets.playback;

import org.jeets.model.traccar.jpa.Position;
import org.jeets.player.PlaybackListener;

public class PlaybackReceiver implements PlaybackListener {

    // Tracker tracker = new Tracker(server, port, toUniqueId);

    @Override
    public void receivePositionEntity(Position positionEntity) {
        System.out.println("PlaybackReceiver received position: " + positionEntity);
//      pass position to tracker
    }

}
