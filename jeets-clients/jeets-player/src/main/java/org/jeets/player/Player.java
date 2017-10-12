package org.jeets.player;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jeets.model.traccar.jpa.Position;
import org.jeets.protocol.Traccar;
import org.jeets.protocol.util.Transformer;
import org.jeets.tracker.ProtoPositionListener;

/**
 * The JeeTS GPS Player provides a simulation and development environment to
 * prepare any GPS data format for protoPositions or jpaPositions.
 * <p>
 * The GPS Player can replay Lists JPA Positions and feed the JeeTS GPS Tracker
 * with protoPositions to send OR it can be used in the system environment to
 * send jpaPosition Entities ...
 * <p>
 * TODO: The Player should not import/depend on the Tracker, i.e not include
 * Netty networking as it will also be used inside the App Server environment,
 * which should not deal with network formats. <br>
 * -> The Player should only deal with Entities, while the Tracker should do the
 * format transformations, which are placed in the jeets-protocol project.
 * 
 * @author kbeigl@jeets.org
 */
public class Player {

    /**
     * Due to limitations of the Java language syntax the position type can not
     * be distinguished with two List<> constructors. Therefore lists with
     * different types (proto and jpa) are not passed via constructor and
     * must be added after the instance was created.
     */
    public Player() {
    }

    /* add useful parameters: 
     * default: startImmediately -> ignore date and time -> use deltas only
     * ignoreDay -> use time of day only
     * live -> play exact time stamps, i.e. fix times
     * sendOriginal time stamps
     * repeat track n times
     * player.set( config ); // speed x2 x4 x0.5 etc.
     *   
     * options: time factor!!, start stop pause FF   << < || > >>
     *  or for transits: play live, 
     *  play daytimes (ignore date,day,weekday,year to watch your daily trip to work)
     */
    public void playProtos() {
//      check, if Protolist is okay and ready ..
        for (int protoPosition = 0; protoPosition < protoPositionBuilders.size(); protoPosition++) {
//          replace Date with 0 and add System.currentTimeMillis()
            Traccar.Position.Builder position = protoPositionBuilders.get(protoPosition);
            System.out.println(new Date() + " pos#" + protoPosition + ": " + position.getFixtime());
//          programTimersForPlayback(position);
            fireProtoPosition(position);
            if (protoPosition < protoPositionBuilders.size()-1) { // exclude last position
                long nextPositionMs = protoPositionBuilders.get(protoPosition+1).getFixtime() - position.getFixtime();
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

    private List<Position> positionEntities;  // = new ArrayList<>();
    private List<Traccar.Position.Builder> protoPositionBuilders;

    public void setJpaPositions(List<Position> jpaPositions) {
        this.positionEntities = jpaPositions;
//      could be done individually later as needed:
        convertJpaToProtoPositions();
    }

//  TODO: REWRITE TO POSTION ENTITIES !!!
    public void setProtoPositionBuilders(List<Traccar.Position.Builder> protoPositions) {
        this.protoPositionBuilders = protoPositions;
//      TODO: Entities are not used as output yet:
//      convertProtoToJpaPositions();
    }

    /**
     * requires existing and valid positionEntities member!
     */
//  Util class ?
    private void convertJpaToProtoPositions() {
        protoPositionBuilders = new ArrayList<>();
        for (Position positionEntity : positionEntities) {
            protoPositionBuilders.add(      
                    // convertJpaToProtoPosition(jpaPosition));
                    Transformer.entityToProtoPosition(positionEntity));
        }
    }

//  Listener concept could be improved with weld CDI !
    private List<ProtoPositionListener> listeners = new ArrayList<ProtoPositionListener>();
    
    public void addListener(ProtoPositionListener protoPositionListener) {
        listeners.add(protoPositionListener);
    }

    private void fireProtoPosition(Traccar.Position.Builder position) {
        System.out.println("transmit device position#" + position + " to listener");
        for (ProtoPositionListener listener : listeners)
            listener.transmitPositionProto(position);
    }

}
