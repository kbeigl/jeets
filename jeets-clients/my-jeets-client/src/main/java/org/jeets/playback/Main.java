package org.jeets.playback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.jeets.model.traccar.jpa.Position;
import org.jeets.playback.database.DatabaseFactory;
import org.jeets.playback.gtfs.GtfsFactoryWithViews;
import org.jeets.player.Player;
import org.jeets.tracker.Tracker;

/**
 * This class provides a simple implementation for three different Track
 * sources. After generating a new project with 'mvn generate' the developer can
 * run each implementation, choose one to work on and delete the others.
 * 
 * @author kbeigl@jeets.org
 */
public class Main {

//  server parameters for all factories
    // static final String HOST = System.getProperty("host", "127.0.0.1");
    // static final int PORT = Integer.parseInt(System.getProperty("port","5200"));
    // load traccar's default.xml for available protocols and ports!!

    /**
     * Different factory implementations to create a Position List. 
     */
    public enum EntityFactory {
        DATABASE, GTFS, GTFS_VIEWS, REST
    }    

    public static void main(String[] args) {

        List<Position> positionEntities = null;
        Main main = new Main();
        // 1. create position list with any factory
        {
//          TODO: extract a single interface for GTFS and REST to Transit Data
            switch (EntityFactory.DATABASE) {
            case DATABASE:
                positionEntities = main.selectPositionsFromDB( /* add params or SQL statement */ );
                break;
            case GTFS_VIEWS:    // temporary, intermediate dev step, to be removed 
//              A factory can also be used to constantly create traffic by creating a player for each vehicle.
                GtfsFactoryWithViews gtfs = new GtfsFactoryWithViews();
//              using carefully an manually selected parameters
//              positionEntities = 
                    gtfs.getNextTrip("U1", "Fuhlsbüttel", "Farmsen", new Date());
//              do this inside gtfsFactory (?)
                gtfs.closeConnection();
                break;
                
//          case GTFS:
//              break;

            case REST:
//              GeoFox -> confidential !
//              move GeoFoxFactory.main here (change Builder- to Entity List)
//              positionBuilders = geoFox.getLiveTrack(lineKey, departureString, viaString, departureOffset);
                break;
            default:
                break;
            }
        }
//      position list ready for playback
        System.out.println(positionEntities.size() + " positions retrieved ");
        
        // 2. prepare Position-Entity List (move this to Player ?!)
        preparePositionsForReplay(positionEntities);
        
        // 3. create Player
        Player player = new Player(positionEntities);

        // 4. create Client with Tracker
        // uniqueId must be registered (for each player) and can defer from database selection
        Tracker tracker = new Tracker("localhost", 5200, "pb.device.echo"); 
        Client receiver = new Client( tracker );
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

}
