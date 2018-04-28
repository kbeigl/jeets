package org.jeets.playback;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jeets.client.MyClientDevice;
import org.jeets.model.traccar.jpa.Position;
import org.jeets.playback.factories.DatabaseFactory;
//import org.jeets.playback.factories.GeoFoxFactory;
import org.jeets.playback.factories.TransitFactory;
import org.jeets.player.Player;
import org.jeets.tracker.Tracker;

//import de.hvv.rest.GeoFoxException;

/**
 * This class provides a simple implementation for three different Track
 * sources. After generating a new project with 'mvn generate' the developer can
 * run each implementation, choose one to work on and delete the others.
 * 
 * @author kbeigl@jeets.org
 */
public class Main {

    /* Dev Note 9.6.17
     * Currently the jar-with-deps requires a manual change
     * as described in RESTEASY003145.txt !!
     * 
     * Dev Note 6.6.17
     * The Factory can be launched from command line for server development
     * currently the time stamps are not LIVE > analyze AND implement Timer scheduling !
     * currently the time stamps are (sometimes?) from the previous day! 
     * -> debug geofox / gtfs (midnight handling)
     * see ...-status.txt file in this project
     */

    /* server parameters for all factories
     * static final String HOST = System.getProperty("host", "127.0.0.1");
     * static final int PORT = Integer.parseInt(System.getProperty("port","5200"));
     * load traccar's default.xml for available protocols and ports!!
     */

    /**
     * Different factory implementations to create a Position List. 
     */
    public enum EntityFactory {
        DATABASE, GTFS, REST
    }    

    public static void main(String[] args) {

        List<Position> positionEntities = null;
        Main main = new Main();
        // A factory can also be used to create traffic 
        // by creating a player for each vehicle constantly.
        
//      1. create position list with any factory
//      manually selected and hard coded transit parameters (works for HVV)
        int routeType = 1;  // should be removed
        String routeShortName = "U1";
        String departureStop = "Farmsen", viaStop = "Fuhlsbüttel";
        String lineKey = "HHA-U:" + routeShortName + "_HHA-U";
        String uniqueId = "U1.FaFu";
            
//      temporarily applied for U1 in both directions
        if (args.length == 4) {
            routeShortName = args[0];
            departureStop = args[1];
            viaStop = args[2];
            uniqueId = args[3];
        }
        {
            Instant depart;
//                  = new Date().toInstant();  // now
//          String startDateString = "2017-11-03T14:30:38Z";
//          WRONG result 18:00 at Farmsen !! correct via GeoFox !!
//          COORECT RESULTS FOR FARMSEN 18:10
//          override with specific time
            String startDateString = "2017-11-03T18:08:00Z";
            depart= Instant.parse(startDateString);   

//          switch manualy during development
            switch (EntityFactory.GTFS) {
            case DATABASE:
//              1a. use Traccar Persistence Unit to query database
//              add input params or SQL statement
                positionEntities = main.selectPositionsFromDB(lineKey);
                break;
            case GTFS:
//              1b. use TransitFactory with access to GTFS API 
                TransitFactory factory = new TransitFactory();
//              routeType could be skipped and a (localized) Date parameter should be added
                positionEntities = factory.getNextTrack(routeType, routeShortName, departureStop, viaStop, depart);
//              positionEntities = factory.getNextTrack(routeType, routeShortName, viaStop, departureStop, depart);
                break;
            case REST:
//              1c. use GeoFoxFactory with access to GeoFox API -> CONFIDENTIAL !
/*
                GeoFoxFactory geoFox = new GeoFoxFactory();
                try {
                    positionEntities = geoFox.getNextTrack(lineKey, departureStop, viaStop, depart);
                } catch (GeoFoxException e) {
                    logger.error("Problems with GeoFox REST service: " + e.getMessage());
                    logger.error("For a manual fix of RESTEASY003145 see RESTEASY003145.txt in this project");
                }
 */
                break;
            default:
                break;
            }
        }
        
//      at this point all shape Positions should exist and 
//      the station for arr and dep (same coord) should have timestamps
//      next the Positions between the stations/stops, i.e. CourseElement/path
//      should be enriched with timestamps
//        see GeoFox.composeCourseTrack for existing code !!
        
        System.out.println(positionEntities.size() + " positions retrieved ");
//      listTrack(positionEntities);
        // this should only be applied to DATABASE 
        // while Transit should be played back by actual time stamps
        adjustFixtimesStartingNow(positionEntities);
        listTrack(positionEntities);
        
//      2. create Player/s
        Player player = new Player(positionEntities);
        
//      3. create Client with Tracker
        // uniqueId must be registered (for each player) and can defer from database selection
        Tracker tracker = new Tracker("localhost", 5200, uniqueId); 
        // client device sends infos via tracker to server port
        MyClientDevice receiver = new MyClientDevice( tracker );
        player.addListener(receiver);
        player.startPlayback();

        System.out.println("End Main");
        return;
    }
    
    private static boolean areEqual(Position gtfs, Position gfox) {
        if (gtfs.getFixtime().equals(gfox)) return true;
//      etc.
        return false;
    }

    private static DateFormat df = new SimpleDateFormat("dd.MM.yy HH:mm:ss");

    private static void listTrack(List<Position> positionEntities) {
        for (int pos = 0; pos < positionEntities.size(); pos++) {
            Position position = positionEntities.get(pos);
            String dateString = "no date";
            if (position.getFixtime() != null) {
                dateString = df.format(position.getFixtime());
            }
            System.out.println( pos + ". " + dateString + "\t" 
                    + position.getLatitude() + "\t" + position.getLongitude() 
                    + "\t" + position.getAddress());
        }
    }

    /**
     * As Fixtimes should not be sent from the past time and they should be set
     * to the time they are actually played to listeners.
     */
    private static void adjustFixtimesStartingNow(List<Position> positionEntities) {
        long msOffset = new Date().getTime() - positionEntities.get(0).getFixtime().getTime();
        for (Position position : positionEntities) {
            // adjust fixtimes starting first position from 'now'
            Date newFixtime = new Date(position.getFixtime().getTime() + msOffset);
            position.setFixtime(newFixtime);
        }
    }

    /**
     * Hard coded parameters for database access.
     * <p>
     * Should be replaced with program logic.
     * @param device 
     */
    private List<Position> selectPositionsFromDB(String device) {

        String jdbcUrl = "jdbc:postgresql://localhost:5432/traccar3.14";
        String persistenceUnit = "jeets-pu-traccar-jpa";
        DatabaseFactory db = new DatabaseFactory(jdbcUrl, persistenceUnit);

//      String fromDevice = "pb.device";
//      Date fromDate = parseDate("2017-05-20 16:10:00");
//      Date   toDate = parseDate("2017-05-20 17:43:00");
        
//      line 'U1' with full geometry :)
//      String device = "HHA-U:U1_HHA-U";
//      data must exist in DB ;)
        Date fromDate = parseDate("2017-06-05 13:09:00");
        Date   toDate = parseDate("2017-06-08 16:36:00");

        List<Position> positionEntities = db.selectPositionList(device, fromDate, toDate);
        System.out.println(positionEntities.size() + " positions " + "for device " + device);

//      DEBUG: main method doesn't terminate (here) > close db, EMgr .. ?

        return positionEntities;
    }
    
    private static Date parseDate(String timestamp) {
        Date date = null;
        String dateFormat = "yyyy-M-dd hh:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        try {
            date = sdf.parse(timestamp);
        } catch (ParseException e) {
            System.err.println("Error parsing the Date String: " + timestamp + " to dataformat: " + dateFormat);
            // usage(); DateFormat
            System.exit(1);
        }
        return date;
    }

    private static Log logger = LogFactory.getLog(Main.class);
}
