package org.jeets.player;

import org.jeets.model.traccar.jpa.Position;

/**
 * SampleReceiver used for demo in main method. The Tracker is the 'normal' receiver of Position
 * Entities filled by the devices sensors.
 *
 * @author kbeigl@jeets.org
 */
//  TODO: move to test folder and add tests
public class SampleReceiver implements PlaybackListener {

  @Override
  public void receivePositionEntity(Position positionEntity) {
    System.out.println("Receiver received position: " + positionEntity);
  }
}
