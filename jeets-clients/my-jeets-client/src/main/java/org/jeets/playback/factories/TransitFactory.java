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

//  database (TODO: PU with persistence.xml)
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
    public List<Position> getNextTrack(int routeType, String routeShortName, 
            String fromStop, String toStop, Instant departAt) {

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

//  this method is a (terrible) hack!
//  TODO: implement geometric operations with JTS
//  align with GeoFoxFactory.composeCourseTrack(List<CourseElement> courseElements)
    private List<Position> createPositionTrip(GtfsTrip trip, ZonedDateTime zonedDateTime) {
        List<GtfsStopTimes> tripStopTimes = gtfs.getStopTimes(trip); // size should match
        List<GtfsStop>      tripStops     = gtfs.getStops    (trip); // size should match
        List<GtfsShape>     tripShapes    = gtfs.getShapes   (trip); 
        if (tripStops.size() != tripStopTimes.size()) {
            logger.error("tripStops and tripStopTimes sizes are NOT equal !?");
//          now what?
        }
        logger.info("trip has " + tripStops.size() + " stops, " 
                + tripStopTimes.size() + " stopTimes and " 
                + tripShapes.size() + " shapes");
//      TODO: check GTFS specs for direction ------------------------
//      GtfsTrip [ .., directionId=1, .. ]
        GtfsShape firstShape = tripShapes.get(0);
        GtfsStop  firstStop  = tripStops .get(0);
        GtfsStop   lastStop  = tripStops .get(tripStops.size()-1);
//      assert same order, i.e. direction of shapes and stops
        int direction = -1;
        if (cartesianDistance(firstShape, firstStop) 
                < cartesianDistance(firstShape, lastStop))
            direction = 1;
        else {
//          TODO: implement opposite directions ?
            System.err.println("shapes and stops have different directions.");
            return null;
        }   
//      -------------------------------------------------------------
        List<Position> positions = new ArrayList<Position>();
        int stopNr = 0; Date arrDate = null;
        GtfsStop stop = null; GtfsStopTimes stopTime = null;
//      createPosition for each Shape
        for (int shapeNr = 0; shapeNr < tripShapes.size(); shapeNr++) { 
            GtfsShape shape = tripShapes.get(shapeNr);
            stop = tripStops.get(stopNr);
            stopTime = tripStopTimes.get(stopNr);

            if (cartesianDistance(shape, stop) < 0.0009d) {   // experimental distance
//              use shape coordinates, override stop coordinates, add stopTime and -Name
                arrDate = createDate( zonedDateTime, stopTime.getArrivalTime() ) ;
                positions.add(createPosition(shape.getShapePtLat(), shape.getShapePtLon(), 
                        arrDate, stopNr + "-" + stop.getStopName()));
                if (stopNr < tripStops.size()-1) 
                     stopNr++;
                else stopNr = 1; // out of reach
            } else {
//              use shape coordinates
//              shape.getShapePtSequence() // unused (?)
                positions.add(createPosition(
                        shape.getShapePtLat(), shape.getShapePtLon(), null, null));
            }
        }
        
        return positions;
    }

    private Position createPosition(double lat, double lon, Date arrDate, String stopName) {
        Position position = new Position();
        position.setLatitude (lat);
        position.setLongitude(lon);
        position.setFixtime(arrDate);
//      Note: Address will not be transmitted (helpful here)
        position.setAddress(stopName);
        return position;
    }

    private double cartesianDistance(GtfsShape shape, GtfsStop stop) {
        return Math.sqrt( 
               Math.pow(shape.getShapePtLat() - stop.getStopLat(), 2) +
               Math.pow(shape.getShapePtLon() - stop.getStopLon(), 2));
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
