package org.jeets.playback;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jeets.client.MyClientDevice;
import org.jeets.model.traccar.jpa.Position;
import org.jeets.playback.factories.DatabaseFactory;
import org.jeets.playback.factories.GeoFoxFactory;
import org.jeets.playback.factories.TransitFactory;
import org.jeets.player.Player;
import org.jeets.tracker.Tracker;

import de.hvv.rest.GeoFoxException;

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
        // 1. create position list with any factory
        {
//          selected transit parameters
            int routeType = 1;
            String routeShortName = "U1";
            String departureStop = "Farmsen", viaStop = "Fuhlsbüttel";
            String lineKey = "HHA-U:" + routeShortName + "_HHA-U";

            Instant depart = new Date().toInstant();  // now
//          override with specific time
//          String startDateString = "2017-11-03 14:30:38";
//          WRONG result 18:00 at Farmsen !! correct via GeoFox !!

            switch (EntityFactory.REST) {
            case DATABASE:
//              2a. use Traccar Persistence Unit to query database
//              add input params or SQL statement
                positionEntities = main.selectPositionsFromDB();
                break;

//              TODO: extract a single interface for GTFS and REST to Transit Data
//              manually selected and hard coded parameters (works for HVV)
            case GTFS:
//              2b. use TransitFactory with access to GTFS API 
                TransitFactory factory = new TransitFactory();
//              routeType could be skipped and a (localized) Date parameter should be added
                positionEntities = factory.getNextTrack(routeType, routeShortName, departureStop, viaStop, depart);
                
                break;
            case REST:
//              2c. use GeoFoxFactory with access to GeoFox API -> CONFIDENTIAL !
                ZoneId currentZone = ZoneId.of("Europe/Berlin");  // hardcoded, may be available
                ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(depart, currentZone);
                GeoFoxFactory geoFox = new GeoFoxFactory();
                try {
                    positionEntities = geoFox.getNextTrack(lineKey, departureStop, viaStop, zonedDateTime);
                } catch (GeoFoxException e) {
                    logger.error("Problems with GeoFox REST service: " + e.getMessage());
                    logger.error("For a manual fix of RESTEASY003145 see RESTEASY003145.txt in this project");
                }
                
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
        
        DateFormat df = new SimpleDateFormat("dd.MM.yy HH:mm:ss"); 
        for (int pos = 0; pos < positionEntities.size(); pos++) {
//      for (int pos = 0; pos < 20; pos++) {
            Position position = positionEntities.get(pos);
            String dateString = df.format(position.getFixtime());
            System.out.println( pos + ". " + dateString + "\t" 
                    + position.getLatitude() + "\t" + position.getLongitude() 
                    + "\t" + position.getAddress());
        }
        
//      position list ready for playback
        System.out.println(positionEntities.size() + " positions retrieved ");
        
        // 2. prepare Position-Entity List (move this to Player ?!)
//      don't do this for a live track for a transit vehicle !! > branch
        setFixtimesStartingNow(positionEntities);
        // optimize track by adding altitude and calculating course and speed
/*
        // 3. create Player
        Player player = new Player(positionEntities);

        // 4. create Client with Tracker
        // uniqueId must be registered (for each player) and can defer from database selection
        Tracker tracker = new Tracker("localhost", 5200, "pb.device.echo"); 
        // client device sends infos via tracker to server port
        MyClientDevice receiver = new MyClientDevice( tracker );
        player.addListener(receiver);
        player.startPlayback();
 */
        System.out.println("End Main");
        return;
    }

    /**
     * As Fixtimes should not be sent from the past time and they should be set
     * to the time they are actually played to listeners.
     */
    private static void setFixtimesStartingNow(List<Position> positionEntities) {
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
     */
    private List<Position> selectPositionsFromDB() {

        String jdbcUrl = "jdbc:postgresql://localhost:5432/traccar3.14";
        String persistenceUnit = "jeets-pu-traccar-jpa";
        DatabaseFactory db = new DatabaseFactory(jdbcUrl, persistenceUnit);

        String fromDevice = "pb.device";
        Date fromDate = parseDate("2017-05-20 16:10:00");
        Date   toDate = parseDate("2017-05-20 17:43:00");
        List<Position> positionEntities = db.selectPositionList(fromDevice, fromDate, toDate);
//      System.out.println(positionEntities.size() + " positions " + "for device " + fromDevice);

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
