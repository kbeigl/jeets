package org.jeets.playback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        // A factory can also be used to constantly create traffic 
        // by creating a player for each vehicle.
        // 1. create position list with any factory
        {
            switch (EntityFactory.REST) {
            case DATABASE:
//              use Traccar Persistence Unit to query database
                
//              add input params or SQL statement
                
                positionEntities = main.selectPositionsFromDB();
                break;

//              TODO: extract a single interface for GTFS and REST to Transit Data
//              manually selected and hard coded parameters (works for HVV)
            case GTFS:
//              use TransitFactory with access to GTFS API 
                int routeType = 1;
                String routeShortName = "U1";
                String stop1 = "Farmsen", stop2 = "Fuhlsbüttel";

                TransitFactory factory = new TransitFactory();
                positionEntities = factory.getNextTrack(routeType, routeShortName, stop1, stop2);
                
                break;
            case REST:
//              use GeoFoxFactory with access to GeoFox API -> CONFIDENTIAL !
                String lineKey = "HHA-U:U1_HHA-U",  
                departureString = "Jungfernstieg",
                viaString = "Hallerstraße";
                int departureOffset = 35; // [min]
                
                GeoFoxFactory geoFox = new GeoFoxFactory();
                try {
                    positionEntities = geoFox.getLiveTrack(lineKey, departureString, viaString, departureOffset);
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
        
//      position list ready for playback
        System.out.println(positionEntities.size() + " positions retrieved ");
        
        // 2. prepare Position-Entity List (move this to Player ?!)
//      don't do this for a live track for a transit vehicle !! > branch
        preparePositionsForReplay(positionEntities);
        // optimize track by adding altitude and calculating course and speed
        
        // 3. create Player
        Player player = new Player(positionEntities);

        // 4. create Client with Tracker
        // uniqueId must be registered (for each player) and can defer from database selection
        Tracker tracker = new Tracker("localhost", 5200, "pb.device.echo"); 
        // client device sends infos via tracker to server port
        MyClientDevice receiver = new MyClientDevice( tracker );
        player.addListener(receiver);
        player.startPlayback();

        System.out.println("End Main");
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
