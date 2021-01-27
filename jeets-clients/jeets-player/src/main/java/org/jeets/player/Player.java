package org.jeets.player;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jeets.model.traccar.jpa.Position;

/**
 * The JeeTS GPS Player provides a simulation and development environment to re/play tracks with
 * latitude, longitude and duration between time stamps.
 *
 * <p>The core of the Player is a list of position entities which can be loaded and replayed. The
 * Player methods can pause, fast forward (not backward!) the running track or specify start and end
 * points and times of the replay.
 *
 * @author kbeigl@jeets.org
 */
public class Player {

  public Player() { // deactivate ?
  }

  private List<Position> positionEntities; // = new ArrayList<>();

  public Player(List<Position> positionEntities) {
    this.positionEntities = positionEntities;
    //      prepare ...
  }

  public void setPositions(List<Position> positionEntities) {
    this.positionEntities = positionEntities;
    //      reset player ...
    //      prepare ...
  }

  /* To decide:
   * 1. run playback in a Thread
   * OR
   * 2. programTimersForPlayback with extensive use of java8 timeapi 'datemath'
   */
  public void startPlayback() {
    //      TODO: create PlaybackThread class, set tracker ...
    //      PlaybackThread playback =
    //      playback.setPositions ..
    Thread t = new Thread(new PlaybackThread(), "PlaybackThread");
    t.start();
  }

  //  public void restartPlayback() { }
  //  public void pausePlayback() { }
  //  public void  stopPlayback() { }

  private class PlaybackThread implements Runnable {
    public void run() {
      for (int positionNr = 0; positionNr < positionEntities.size(); positionNr++) {
        //              replace Date with 0 and add System.currentTimeMillis()
        Position positionEntity = positionEntities.get(positionNr);
        System.out.println(
            new Date() + "sending pos#" + positionNr + " fixed at " + positionEntity.getFixtime());
        sendPosition(positionEntity);

        if (positionNr < positionEntities.size() - 1) {
          // exclude last position (-1) and use next (+1) position to determine millis
          long nextPositionMs =
              positionEntities.get(positionNr + 1).getFixtime().getTime()
                  - positionEntity.getFixtime().getTime();
          System.out.println("Player sending next position in " + nextPositionMs + " ms");
          try {
            Thread.sleep(nextPositionMs);
          } catch (Exception e) {
            System.err.println("Exception during wait for next position");
          }
        }
      }
      System.out.println("Player stopped: Reached end of track");
    }
  }

  /**
   * Send the current (timestamp 'now') position to listeners.
   *
   * @param positionEntity
   */
  private void sendPosition(Position positionEntity) {
    for (PlaybackListener listener : listeners) listener.receivePositionEntity(positionEntity);
  }

  private void programTimersForPlayback() {
    //      args: Position or PositionBuilder(devTime!) .. ?
    //      Timer timer = new Timer();
    //      timer.schedule(task, from, to);
    //        new Timer().scheduleAtFixedRate(new TimerTask() {
    //            @Override
    //            public void run() {
    //                try {
    //                    Context.getDataManager().clearPositionsHistory();
    //                } catch (SQLException error) {
    //                    Log.warning(error);
    //                }
    //            }
    //        }, 0, CLEAN_PERIOD);
  }

  private List<PlaybackListener> listeners = new ArrayList<PlaybackListener>();

  public void addListener(PlaybackListener playbackListener) {
    listeners.add(playbackListener);
  }
}
