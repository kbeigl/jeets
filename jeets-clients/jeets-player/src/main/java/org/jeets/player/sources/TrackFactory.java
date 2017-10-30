package org.jeets.player.sources;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TemporalType;

import org.jeets.model.traccar.jpa.Position;
import org.jeets.player.Player;
import org.jeets.player.SampleReceiver;

/**
 * This class was moved and reengineered in my-jeets-client as
 * DatabaseFactory. Create a test case with h2, insert mini track and replay to
 * SampleReceiver as a template for a DBfactory...
 * 
 * @author kbeigl@jeets.org
 */
@Deprecated
public class TrackFactory {

//  String[] args:
    private String server; // = "127.0.0.1"; // "demo.traccar.org"
    private int port; // = 5200; // <entry key='pb.device.port'>5200</entry>
    private String fromUniqueId; // = "pb.device";
    private String toUniqueId;   // = "pb.device.echo";
    private Date fromDate, toDate;
    private String fromDay, fromTime, toDay, toTime;
//    private String fromDate = "2017-05-20", fromTime = "16:10:00";
//    private String   toDate = "2017-05-20",   toTime = "17:43:00";

    String jdbcUrl = "jdbc:postgresql://localhost:5432/traccar3.14";
    String hbm2ddlAuto = "create";  // "create-drop";
    String persistenceUnit = "jeets-pu-traccar-jpa";

//  static final String HOST = System.getProperty("host", "127.0.0.1");
//  static final int PORT = Integer.parseInt(System.getProperty("port","5200"));
//  load traccar's default.xml for available protocols and ports!!

    public static void main(String[] args) 
    {
//      1. create Position-Entity List
        TrackFactory db = new TrackFactory();
        db.parseArgs(args);
        List<Position> positionEntities = 
                db.selectPositionList(db.fromUniqueId, db.fromDate, db.toDate);
        System.out.println(positionEntities.size() + " positions retrieved "
                + "for device " + db.fromUniqueId);

//      2. prepare Position-Entity List
        preparePositionsForReplay(positionEntities);

//      3. create Player
        Player player = new Player(positionEntities);
//      player.setPositionEntities(positionEntities);

//      4. create Tracker as Player Listener !
        SampleReceiver receiver = new SampleReceiver();
        player.addListener(receiver);
//      Tracker tracker = new Tracker(pgFactory.server, pgFactory.port, pgFactory.toUniqueId);
//      player.addListener(tracker);

//      5. play
        player.startPlayback();

//      6. done
        System.out.println("End DatabaseFactory");
        return;
    }

    /**
     * Modify Positions for 'live' playback.
     * <p>
     * The Fixtimes should not be sent from the past time and are therefore set
     * to the time they are actually played to listeners. <br>
     * Many other modifications according to the setup can be added as needed.
     */
    private static void preparePositionsForReplay(List<Position> positionEntities) {
        long msOffset = new Date().getTime() - positionEntities.get(0).getFixtime().getTime();
        for (Position position : positionEntities) {
            // adjust fixtimes starting first position from 'now'
            Date newFixtime = new Date(position.getFixtime().getTime() + msOffset);
            position.setFixtime(newFixtime);
            // clear attributes {..} to null
            // not needed: clear servertime
        }
    }

    /**
     * Immediately start submitting GPS Positions to Tracker
     * according to real time stamp deltas.
     */
    @Deprecated
    private void play(List<Position> positions) {
        for (int pos = 0; pos < positions.size(); pos++) {
//          replace Date with 0 and add System.currentTimeMillis()
            Position position = positions.get(pos);
            System.out.println(new Date() + " pos#" + pos + ": " + position.getFixtime());
//          String gps103Position = convertPositionEntityToGps103Message(positions.get(pos));
            // traccar default gps103.port: 5001 valid for: "demo.traccar.org"
            
//          Tracker.transmitProtocolString(gps103Position, server, 5001);   // hard coded !

            if (pos < positions.size()-1) { // exclude last position
                long nextPositionMs = positions.get(pos+1).getFixtime().getTime() -
                        position.getFixtime().getTime();
                System.out.println("sending next position in " + nextPositionMs + " ms");
                try {
                    Thread.sleep(nextPositionMs);
                } catch (Exception e) {
                    System.err.println("Exception during wait for next position");
                }
            }
        }
        System.out.println("Player stopped: Reached end of track");
    }

    private List<Position> selectPositionList(String uniqueId, Date fromDate, Date toDate) {
        
        createEntityManager();

        String sql = "select p from Position p "
                + "left join fetch p.device d "
                + "where d.uniqueid = :uniqueid "
                + "and p.fixtime between :from and :to "
                + "order by p.fixtime";

        List<Position> positions = entityManager
                .createQuery(sql, org.jeets.model.traccar.jpa.Position.class)
                .setParameter("uniqueid", uniqueId)
                .setParameter("from", fromDate, TemporalType.TIMESTAMP)
                .setParameter("to", toDate, TemporalType.TIMESTAMP)
                .getResultList();
        // catch noResult (null?)

        entityManager.close();
        entityManagerFactory.close();

        return positions;
    }

    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    private void createEntityManager() {
        Map<String, Object> overrideConfig = new HashMap<String, Object>();
        // choose your database
        overrideConfig.put("javax.persistence.jdbc.url", jdbcUrl);
        // overrideConfig.put("hibernate.hbm2ddl.auto", hbm2ddlAuto);
        entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnit, overrideConfig);
//      entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnit);
        // override persistence properties as described in Book Sec 6.4.1 !!
        System.out.println(entityManagerFactory.getProperties());
        entityManager = entityManagerFactory.createEntityManager();
    }

    private Date parseDate(String timestamp) {
        Date date = null;
        String dateFormat = "yyyy-M-dd hh:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        try {
            date = sdf.parse( timestamp );
        } catch (ParseException e) {
            System.err.println("Error parsing the Date String: " + timestamp + " to dataformat: " + dateFormat);
//          usage(); DateFormat
            System.exit(1);
        }
        return date;
    }

    private void parseArgs(String[] args) {
        // if (args.length .. etc) {
//      sample: pb.device 2017-05-20 16:10:00 2017-05-20 17:43:00 localhost 5200 pb.device.echo
        fromUniqueId = args[0];
        fromDay = args[1];
        fromTime = args[2];
        toDay = args[3];
        toTime = args[4];
        server = args[5];
        try {
            port = Integer.parseInt(args[6]);
        } catch (NumberFormatException e) {
            System.err.println("port" + args[6] + " must be an integer.");
            System.exit(1);
        }
        toUniqueId = args[7];
        
        fromDate = parseDate(fromDay + " " + fromTime);
        toDate   = parseDate(toDay   + " " + toTime);
    }

}
