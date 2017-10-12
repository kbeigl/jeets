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
import org.jeets.tracker.Tracker;
import org.jeets.tracker.Util;

public class HibernateFactory {

//  String[] args:
    private String server; // = "127.0.0.1"; // "demo.traccar.org"
    private int port; // = 5200; // <entry key='pb.device.port'>5200</entry>
    private String fromUniqueId; // = "pb.device";
    private String toUniqueId;   // = "pb.device.echo";
    private String fromDate, fromTime, toDate, toTime;
//    private String fromDate = "2017-05-20", fromTime = "16:10:00";
//    private String   toDate = "2017-05-20",   toTime = "17:43:00";

    String jdbcUrl = "jdbc:postgresql://localhost:5432/traccar3.14",
    hbm2ddlAuto = "create",  // "create-drop";
    persistenceUnit = "jeets-pu-traccar-jpa";

//  static final String HOST = System.getProperty("host", "127.0.0.1");
//  static final int PORT = Integer.parseInt(System.getProperty("port","5200"));
//  load traccar's default.xml for available protocols and ports!!

    public static void main(String[] args) 
    {
//      1. create Position-Entity List
        HibernateFactory pgFactory = new HibernateFactory();
        pgFactory.parseArgs(args);
        Date from = pgFactory.parseDate(pgFactory.fromDate + " " + pgFactory.fromTime);
        Date to   = pgFactory.parseDate(pgFactory.toDate   + " " + pgFactory.toTime);
        List<Position> jpaPositions = pgFactory.selectPositionList(pgFactory.fromUniqueId, from, to);
        System.out.println(jpaPositions.size() + " positions retrieved from database.");
//      adjust fixtimes starting first position from 'now'
        long msOffset = new Date().getTime() - jpaPositions.get(0).getFixtime().getTime();
        for (Position position : jpaPositions) {
            Date newFixtime = new Date (position.getFixtime().getTime() + msOffset );
            position.setFixtime(newFixtime);
        }

//      2. create Player
        Player player = new Player();
        player.setJpaPositions(jpaPositions);
//      player.setJpaPositionBuilders(jpaPositions);

//      3. create Tracker as Player Listener !
        Tracker tracker = new Tracker(pgFactory.server, pgFactory.port, pgFactory.toUniqueId);
        player.addListener(tracker);

//      4. play
//      pgFactory.play(jpaPositions);   // deprecated
        player.playProtos();

        return;
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
            String gps103Position = convertPositionEntityToGps103Message(positions.get(pos));
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

    /**
     * Create single GPS position message:
     * "imei:359587010124900,tracker,809231329,13554900601,F,132909.397,A,2234.4669,N,11354.3287,E,0.11,"
     * 
     * @param position
     * @return
     */
    private static String convertPositionEntityToGps103Message(Position position) {
        // position.getDevices().getId(); don't use
        // empty relation?
        // String uniquId = position.getDevices().getUniqueid();
        // StringBuilder message = new StringBuilder("imei:" + uniquId + ",");
        StringBuilder message = new StringBuilder("imei:359587010124999,"); 
        // message.append("tracker,"); // regular message
        // message.append("help me,"); // alarm message
        message.append(","); // empty message

        // message.append("0809231929,"); // yymmdd
        // localHours=19,localMinutes=29
        // position.getFixtime(); // not applied ?

        Date devTime = position.getDevicetime();
        int y = devTime.getYear() - 100; // 2008 - 1900 - 100 = 8 > one digit
        if (y < 10)
            message.append("0" + y); // one
        else
            message.append(y); // two digits
        int m = devTime.getMonth() + 1;
        if (m < 10)
            message.append("0" + m); // one
        else
            message.append(m); // two digits
        int d = devTime.getDate();
        if (d < 10)
            message.append("0" + d); // one
        else
            message.append(d); // two digits
        int tz = devTime.getTimezoneOffset(); // ignored
        // devTime.toGMTString();
        int h = devTime.getHours();
        if (h < 10)
            message.append("0" + h); // one
        else
            message.append(h); // two digits
        m = devTime.getMinutes();
        if (m < 10)
            message.append("0" + m); // one
        else
            message.append(m); // two digits
        message.append(",");

        message.append("13554900601,F,"); // phone#, rfid ?

        // message.append("112909.397"); // hhmmss.millis
        h = devTime.getHours();
        if (h < 10)
            message.append("0" + h); // one
        else
            message.append(h); // two digits
        m = devTime.getMinutes();
        if (m < 10)
            message.append("0" + m); // one
        else
            message.append(m); // two digits
        int s = devTime.getSeconds();
        if (s < 10)
            message.append("0" + s); // one
        else
            message.append(s); // two digits
        message.append(".");
        long ms = (devTime.getTime());
        String millis = (ms + "");
        millis = millis.substring(millis.length() - 3);
        message.append(millis);
        message.append(",A,"); // A = valid

        // message.append("2234.4669,N,11354.3287,E,0.11,");
        double latDec = position.getLatitude();
        message.append(Util.convertWGS84to_dddmm_mmmm(latDec) + ",");
        String latDir = "S";
        if (latDec > 0)
            latDir = "N";
        message.append(latDir + ",");

        double lonDec = position.getLongitude();
        message.append(Util.convertWGS84to_dddmm_mmmm(lonDec) + ",");
        String lonDir = "W";
        if (lonDec > 0)
            lonDir = "E";
        message.append(lonDir + ",");

        message.append("0.11,"); // distance
        return message.toString();
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
        fromDate = args[1];
        fromTime = args[2];
        toDate = args[3];
        toTime = args[4];
        server = args[5];
        try {
            port = Integer.parseInt(args[6]);
        } catch (NumberFormatException e) {
            System.err.println("port" + args[6] + " must be an integer.");
            System.exit(1);
        }
        toUniqueId = args[7];
    }

}
