package org.jeets.playback;

import org.jeets.model.traccar.jpa.Position;
import org.jeets.player.PlaybackListener;
import org.jeets.tracker.Tracker;

public class Client implements PlaybackListener {

    private Tracker tracker;

    public Client(Tracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public void receivePositionEntity(Position positionEntity) {
        System.out.println("Client received position: " + positionEntity);
        tracker.sendPositionEntity(positionEntity);
    }

}
