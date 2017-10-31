package org.jeets.playback.factories;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jeets.model.gtfs.GtfsAgency;
import org.jeets.model.gtfs.GtfsRoute;
import org.jeets.model.gtfs.GtfsShape;
import org.jeets.model.gtfs.GtfsStop;
import org.jeets.model.gtfs.GtfsStopTimes;
import org.jeets.model.gtfs.GtfsTrip;
import org.jeets.model.traccar.jpa.Position;
import org.jeets.playback.sources.GtfsApi;

/**
 * Generate Traffic of Transit Vehicles specified in a GTFS database.
 * <p>
 * This class was developed with data from 'Hamburger Verkehrsverbund (HVV) at
 * www.hvv.de'. The code should also work for other GTFS sets for other cities.
 * <p>
 * This is a generalized GTFS Factory (General Transit Feed Specification) to
 * create any transit vehicle from GTFS deliveries.
 * <p>
 * The GeoFox Factory is also based on GTFS, although it is accessed via REST
 * service interface. It should be used as the template for generalization and
 * to create standard interfaces for Transit Vehicles.
 *
 * @author kbeigl@jeets.org
 */
/*
 * developer note. This (demo) class currently uses the ResultSet as return
 * value to represent Entities from a PU to be created ...
 */
public class TransitFactory {

//  database (will be PU with persistence.xml)
    private static String host = "127.0.0.1";
    private static int port = 5432;
    private static String dbName = "HVV-20171006";
    private static String dbUser = "postgres";
    private static String dbPassword = "postgres";
    private static String dbUrl = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
//  "jdbc:postgresql://127.0.0.1:5432/HVV-20171006", "postgres", "postgres");
    
//      TODO: create auto detection of highly frequented stops 
//      (pivot points) of a route/trip !!
//      try to determine most frequented fraction of a route 
//      also analyze parent_stations with many children ...

    public TransitFactory() {
        // create database access to GTFS model
        gtfs = new GtfsApi(dbUrl, dbUser, dbPassword);
    }
    
    private GtfsApi gtfs;
    
    /**
     * This method demonstrates a 'top down' approach to generate transit
     * traffic from a GTFS set.
     */
    public List<Position> getNextTrack(int routeType, String routeShortName, String stop1, String stop2) {
        //      create database access to GTFS model ----
//                GtfsApi gtfs = new GtfsApi(dbUrl, dbUser, dbPassword);
        //      lookup transit routes, i.e. 'lines' to generate traffic
                String description = gtfs.getRouteType(routeType);
                logger.info("Creating traffic for " + description);
        
        //      route/s ---------------------------------
        //      get all routes for routeType ..
                List<GtfsRoute> routes = gtfs.getRoutes(routeType);
        //      .. and pick one by short name (later: loop over all)
                GtfsRoute selectedRoute = null;
                for (GtfsRoute route : routes) {
                    if (route.getRouteShortName().equals(routeShortName))
                        selectedRoute = route;
                }
                logger.info("selected Route: " + selectedRoute.getRouteShortName() + ": "
                            + selectedRoute.getRouteLongName());
        
        //      agency ----------------------------------
        //      add agency for legal notice, URL, language and time zone
                GtfsAgency agency = gtfs.getRouteAgency(selectedRoute.getAgencyId());
                logger.info("For agency " +  agency);
                    
        //      stops -----------------------------------
                List<GtfsStop> stops1 = null, stops2 = null;
                stops1 = gtfs.findStationsInRoute(stop1, selectedRoute.getRouteId());
                if (stops1 == null) {
                    logger.error("No stop '" +  stop1 + "' found in route " + selectedRoute.getRouteId());
                    return null; // can't continue
                }
                stops2 = gtfs.findStationsInRoute(stop2, selectedRoute.getRouteId());
                if (stops2 == null) {
                    logger.error("No stop '" +  stop2 + "' found in route " + selectedRoute.getRouteId());
                    return null; // can't continue
                }
                logger.info("Found " +  (stops1.size() + stops2.size()) 
                        + " valid stops in route " + selectedRoute.getRouteId());
                    
        //      convert PC time to agencies time zone
                Date departure = new Date();    // now
                Instant startAt = departure.toInstant();
                ZoneId currentZone = ZoneId.of(agency.getAgencyTimezone());
                ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(startAt, currentZone);
                logger.info("Departure " +  zonedDateTime); 
        
        //      trips -----------------------------------
        //      align with GeoFoxFactory.getLiveTrack
        //      from proprietary format
                GtfsTrip trip = gtfs.getNextTrip(selectedRoute, stops1, stops2, zonedDateTime);
                logger.info("Found " + trip);
        
        //      Once a trip is identified additional infos can be gathered from GTFS.
        //      to jeets format
                List<Position> positionEntities = createPositionTrip(gtfs, trip);
                logger.info("Created " + positionEntities.size() + " positions");
                
                return positionEntities;
    }

//  align with GeoFoxFactory.composeCourseTrack(List<CourseElement> courseElements)
    private List<Position> createPositionTrip(GtfsApi gtfs, GtfsTrip trip) {

        List<GtfsStopTimes> tripStopTimes = gtfs.getStopTimes(trip);
        List<GtfsStop> tripStops = gtfs.getStops(trip); // size should match
        List<GtfsShape> tripShapes = gtfs.getShapes(trip); // size should match
        logger.info("trip has " + tripStops.size() + " stops " 
                + "and " + tripStopTimes.size() + " stopTimes " 
                + "and " + tripShapes.size() + " shapes");

        List<Position> positions = new ArrayList<Position>();

        for (GtfsShape shape : tripShapes) {
            Position position = new Position();
            position.setLatitude(shape.getShapePtLat());
            position.setLongitude(shape.getShapePtLon());
            // shape.getShapePtSequence() // not used (?)
            positions.add(position);
        }

        for (GtfsStop stop : tripStops) {
//             Position position = findClosestPosition(stop, positions);
//             position.setAddress(stop.getStopName());
             
//             stop.getStopId() > lookup stop times
        }
        
//      for (int pos = 0; pos < positions.size(); pos++) {
        for (int pos = 0; pos < 20; pos++) {
            Position position = positions.get(pos);
            System.out.println(position.getLatitude() + "\t" + position.getLongitude() + "\t" + position.getAddress());
        }
        
        return positions;
    }

    private static Log logger = LogFactory.getLog(TransitFactory.class);

}
