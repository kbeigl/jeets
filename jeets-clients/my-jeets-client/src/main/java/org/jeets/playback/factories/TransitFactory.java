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
 * to create standard interfaces for Transit Vehicles. In general the
 * proprietary API should remain in the factory class.
 *
 * @author kbeigl@jeets.org
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
     * traffic from a GTFS set. The parameters should be familiar to transit
     * passengers without API internals.
     */
    public List<Position> getNextTrack(int routeType, String routeShortName, String fromStop, String toStop, Instant departAt) {

        // lookup transit routes, i.e. 'lines' to generate traffic
        String description = gtfs.getRouteType(routeType);
        logger.info("Creating traffic for " + description);

        // route/s ---------------------------------
        List<GtfsRoute> routes = gtfs.getRoutes(routeType);
        GtfsRoute selectedRoute = null;
        for (GtfsRoute route : routes) {
            if (route.getRouteShortName().equals(routeShortName))
                selectedRoute = route;
        }
        logger.info("selected Route: " + selectedRoute.getRouteShortName() + ": " + selectedRoute.getRouteLongName());

        // stops -----------------------------------
        List<GtfsStop> fromStops = null, toStops = null;
        fromStops = gtfs.findStationsInRoute(fromStop, selectedRoute.getRouteId());
        if (fromStops == null) {
            logger.error("No stop '" + fromStop + "' found in route " + selectedRoute.getRouteId());
            return null; // can't continue
        }
        toStops = gtfs.findStationsInRoute(toStop, selectedRoute.getRouteId());
        if (toStops == null) {
            logger.error("No stop '" + toStop + "' found in route " + selectedRoute.getRouteId());
            return null; // can't continue
        }
        logger.debug("Found " + (fromStops.size() + toStops.size()) + " valid stops in route " + selectedRoute.getRouteId());

        // agency ----------------------------------
        // for legal notice, URL, language and time zone
        GtfsAgency agency = gtfs.getRouteAgency(selectedRoute.getAgencyId());
        logger.info("For agency " + agency);

        // convert PC time to agencies time zone
        ZoneId currentZone = ZoneId.of(agency.getAgencyTimezone());
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(departAt, currentZone);
        logger.info("Departure " + zonedDateTime);

        // trips -----------------------------------
        // align with GeoFoxFactory.getLiveTrack
        // from proprietary format ..
        GtfsTrip trip = gtfs.getNextTrip(selectedRoute, fromStops, toStops, zonedDateTime);
        logger.info("Found " + trip);

        // Once a trip is identified additional infos can be gathered from GTFS.
        // .. to jeets format
        List<Position> positionEntities = createPositionTrip(trip, zonedDateTime);
        logger.info("Created " + positionEntities.size() + " positions");

        return positionEntities;
    }

//  align with GeoFoxFactory.composeCourseTrack(List<CourseElement> courseElements)
    private List<Position> createPositionTrip(GtfsTrip trip, ZonedDateTime zonedDateTime) {

        List<GtfsStopTimes> tripStopTimes = gtfs.getStopTimes(trip); // size should match
        List<GtfsStop> tripStops = gtfs.getStops(trip);              // size should match
        List<GtfsShape> tripShapes = gtfs.getShapes(trip); 
        if (tripStops.size() != tripStopTimes.size()) {
            logger.error("tripStops and tripStopTimes sizes are NOT equal !?");
        }
        logger.info("trip has " + tripStops.size() + " stops " 
                + "and " + tripStopTimes.size() + " stopTimes " 
                + "and " + tripShapes.size() + " shapes");

//      MAYBE COLLECT STOP POSITIONS IN A QUEUE TO SNAP SHAPES LATER ??
        List<Position> positions = new ArrayList<Position>();
        
        for (int i = 0; i < tripStops.size(); i++) {
            
            GtfsStop stop = tripStops.get(i);
            GtfsStopTimes stopTime = tripStopTimes.get(i);
            Position position = null;
            
            // skip first arrival position
//            if (i > 0) { // arrival
                position = new Position();
                position.setLatitude (stop.getStopLat());
                position.setLongitude(stop.getStopLon());
                Date arrDate = createDate( zonedDateTime, stopTime.getArrivalTime() ) ;
                position.setFixtime(arrDate);
//              Note: Address will not be transmitted (helpful here)
                position.setAddress(stop.getStopName());
                positions.add(position);
//            }
            // skip last departure position
//            if (i < tripStops.size() - 2) { // departure
                position = new Position();
                position.setLatitude(stop.getStopLat());
                position.setLongitude(stop.getStopLon());
                Date depDate = createDate( zonedDateTime, stopTime.getDepartureTime() ) ;
                position.setFixtime(depDate);
                position.setAddress(stop.getStopName());
//            }

        }
        
//      Position position = findClosestPosition(stop, positions);

/*
        for (GtfsShape shape : tripShapes) {
            Position position = new Position();
            position.setLatitude(shape.getShapePtLat());
            position.setLongitude(shape.getShapePtLon());
            // shape.getShapePtSequence() // not used (?)
            positions.add(position);
        }
 */
        return positions;
    }

    private Date createDate(ZonedDateTime zonedDateTime, String arrivalTime) {
        Date date = Date.from ( zonedDateTime.toInstant() ) ;
        String[] time = arrivalTime.split(":");
//      TODO: GTFS hours can be > 23 (up to 29 found)
        date.setHours(Integer.parseInt(time[0]));
        date.setMinutes(Integer.parseInt(time[1]));
        date.setSeconds(Integer.parseInt(time[2]));
        return date;
    }

    private static Log logger = LogFactory.getLog(TransitFactory.class);

}
