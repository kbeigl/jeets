package org.jeets.player;

import org.jeets.model.traccar.jpa.Position;

/**
 * Any object can receive Position Entities from the playback.
 *
 * @author kbeigl@jeets.org
 */
public interface PlaybackListener {

  void receivePositionEntity(Position positionEntity);
}
