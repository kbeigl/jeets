package org.jeets.playback.sources;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jeets.model.gtfs.GtfsAgency;
import org.jeets.model.gtfs.GtfsRoute;
import org.jeets.model.gtfs.GtfsShape;
import org.jeets.model.gtfs.GtfsStop;
import org.jeets.model.gtfs.GtfsStopTimes;
import org.jeets.model.gtfs.GtfsTrip;

/**
 * API to access a GTFS database.
 * 
 * @author kbeigl@jeets.org
 */
/*
 * developer note! This class will be moved to a separate maven project.
 * Currently the code works with plain SQL which should be replaced with a GTFS
 * Persistence Unit and JPA code. The module MUST also work in an AppServer
 * environment.
 * 
 * Then the Traffic is only running inside this server to provide an
 * environment. For example as a 'Scotland Yard Server' that can be connect to
 * other servers, i.e. cities. The HVV dataset provides Hamburg's Main Station
 * and Airport which can be used to leave town ...
 * 
 * Methods will be modified to return GTFS Entities from the new PU.
 */
public class GtfsApi {

//  TODO: extract Gtfs API, Interface and apply on top of GeoFox REST API
//  TODO: create Gtfs Persistence Unit a rewrite factory with Gtfs Entities
//        create Foreign Keys in database before generating PU

    public List<GtfsShape> getShapes(GtfsTrip trip) {

        String query = "select * from shapes "
                + " where shape_id = '" + trip.getShapeId() + "' "
                + " order by shape_pt_sequence";

        List<GtfsShape> tripShapes = new ArrayList<GtfsShape>();

        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                GtfsShape shape = new GtfsShape();
                shape.setShapeId(rs.getString("shape_id"));
                shape.setShapePtSequence(rs.getInt("shape_pt_sequence"));
                shape.setShapePtLat(rs.getDouble("shape_pt_lat"));
                shape.setShapePtLon(rs.getDouble("shape_pt_lon"));
                tripShapes.add(shape);
            }
            stmt.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        logger.info("found " + tripShapes.size() + " shapes.");

        return tripShapes;
    }

    public List<GtfsStop> getStops(GtfsTrip trip) {

        String query = "select s.*, stop_sequence "
                + "  from stops s, stop_times st "
                + " where s.stop_id = st.stop_id "
                + "   and trip_id = '" + trip.getTripId() + "'"
                + " order by stop_sequence";

        List<GtfsStop> stops = getStops(query);

        return stops;
    }

    public List<GtfsStopTimes> getStopTimes(GtfsTrip trip) {

        String query = "select * from stop_times "
                + " where trip_id = '" + trip.getTripId() + "'"
                + " order by stop_sequence";

        List<GtfsStopTimes> stopTimes = new ArrayList<>();

        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                GtfsStopTimes stop = new GtfsStopTimes();
                stop.setTripId(rs.getString("trip_id"));
                stop.setStopId(rs.getString("stop_id"));
                stop.setStopSequence(rs.getInt("stop_sequence"));
                stop.setArrivalTime(rs.getString("arrival_time"));
                stop.setDepartureTime(rs.getString("departure_time"));
                stop.setStopHeadsign(rs.getString("stop_headsign"));
                stop.setPickupType(rs.getInt("pickup_type"));
                stop.setDropOffType(rs.getInt("drop_off_type"));
                stopTimes.add(stop);
            }
            stmt.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        logger.info("found " + stopTimes.size() + " stops with times.");

        return stopTimes;
    }

    /*
     * The connection between a Route and a StopTime is a Trip.
     * A StopTime has a Trip (trip_id) and a Trip has a Route (route_id).
     */
    public GtfsTrip getNextTrip(GtfsRoute route, List<GtfsStop> fromStops, List<GtfsStop> toStops,
            ZonedDateTime zonedDeparture) {

//      preparations
        List<String> serviceIds = getServicesForDay(zonedDeparture);
        StringBuffer ids = new StringBuffer("(");
        for (String serviceId : serviceIds) {
            ids.append( "'" + serviceId + "'," );
        }
        ids.deleteCharAt(ids.length()-1);
        ids.append( ")" );
        
//      by using from- an toStopIds the direction can be ignored in the query
        String fromStopIds = createStopIdString(fromStops);
        String   toStopIds = createStopIdString(  toStops);
        
        String departureTime = DateTimeFormatter.ofPattern("HH:mm:ss").format(zonedDeparture);
        
//      TODO: account for midnight !!
//      if you want to find all trips between 12:00 AM and 1:00 AM on 30 Jan. 2014, 
//      then you need to search:
//      Between 00:00:00 and 01:00:00 for trips with service on 20140130
//      Between 24:00:00 and 25:00:00 for trips with service on 20140129
//      In Searching for Trips (page 53) you can see how to apply this to your trip searches.
        String query = "SELECT t.* "    // trip entity
                + "  FROM route_trips t, stop_times dep, stop_times arr "
                + " WHERE t.service_id IN " + ids
//              departures (from)
                + "   AND dep.trip_id = t.trip_id "
                + "   AND dep.stop_id  IN  " + fromStopIds
//              TODO cast zonedDateTime to zoneTime to formatted String
                + "   AND dep.departure_time >= '" + departureTime + "' "
//              + "   AND dep.departure_time >= '14:00:00' "
//              + "   AND dep.departure_time <= '14:10:00' "
                + "   AND dep.pickup_type = 0 "
//              arrivals (to)
                + "   AND arr.trip_id = t.trip_id "
                + "   AND arr.stop_id  IN " + toStopIds
//              only works if duration is (roughly) known
//              + "   AND arr.arrival_time >= '14:40:00' "
//              + "   AND arr.arrival_time <= '14:50:00' "
                + "   AND arr.drop_off_type = 0 "
                + "   AND dep.departure_time < arr.arrival_time "
                + " ORDER BY dep.departure_time "
                + " LIMIT 1";   // get first, i.e. next trip only
        
        GtfsTrip trip = new GtfsTrip();
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            // should be a single entity
            while (rs.next()) {
                trip.setTripId(rs.getString("trip_id"));
                trip.setRouteId(rs.getString("route_id"));
                trip.setServiceId(rs.getString("service_id"));
                trip.setDirectionId(rs.getInt("direction_id"));
                trip.setShapeId(rs.getString("shape_id"));
                trip.setHeadsign(rs.getString("trip_headsign"));
            }
            stmt.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return trip;
    }

    /**
     * Creates a String to insert into SQL statement.
     */
    private String createStopIdString(List<GtfsStop> fromStops) {
        StringBuffer stopIds = new StringBuffer("(");
        for (GtfsStop stop : fromStops) {
            stopIds.append( "'" + stop.getStopId() + "'," );
        }
        stopIds.deleteCharAt(stopIds.length()-1);
        stopIds.append( ")" );
        return stopIds.toString();
    }

    private List<String> getServicesForDay(ZonedDateTime zonedDateTime) {

        String day = DateTimeFormatter.ofPattern("yyyyMMdd").format(zonedDateTime);
        String weekday = getWeekday(zonedDateTime);
//      logger.info("Query date (weekday): " + queryDay + " (" + queryWeekday + ")");
//      1. main set of service IDs for the day
//      2. EXCEPT service IDs to be excluded  
//      3. UNION  service IDs to be added
        String query = "SELECT calendar.service_id"
                + "  FROM calendar "
                + " WHERE start_date <= '" + day + "' "
                + "   AND   end_date >= '" + day + "' "
                + "   AND " + weekday + " = true "
                + "       UNION "
                + "SELECT service_id "
                + "  FROM calendar_dates "
                + " WHERE date = '" + day + "' "
                + "   AND exception_type = 2 "
                + "       EXCEPT "
                + "SELECT service_id "
                + "  FROM calendar_dates "
                + " WHERE date = '" + day + "' "
                + "   AND exception_type = 1";
        
        List<String> servicesForDay = new ArrayList<>();
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            servicesForDay.add(rs.getString("service_id"));
        }
        stmt.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return servicesForDay;
    }

    /**
     * Find one or more stops by name.
     * <p>
     * The number of stops can be more than one. Returning all stops is
     * convenient as the trip direction is implicitly detected by a query
     * between to given stop sets.
     * 
     * @param stopName must exist in route
     * @param routeId
     * @return a list of stops with stopName
     */
    public List<GtfsStop> findStationsInRoute(String stopName, String routeId) {
//      retrieve stop_direction from route_stops, order unclear
        String query = "select st.*, rs.direction_id stop_direction "  // , rs.order
                + " from stops st, route_stops rs "
                + "where rs.stop_id = st.stop_id "
                + "  and st.location_type = 0 "
                + "  and st.stop_name = '" + stopName + "' "
                + "  and route_id = '" + routeId + "'";
        
        return getStops(query);
    }

    private List<GtfsStop> getStops(String query) {
        List<GtfsStop> stops = null;
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            if (stops == null)
                stops = new ArrayList<>();

            GtfsStop stop = new GtfsStop();
            stop.setStopId(rs.getString("stop_id"));
            stop.setStopName(rs.getString("stop_name"));
            stop.setParentStation(rs.getString("parent_station"));
            stop.setStopLocType(rs.getDouble("location_type"));
            stop.setWheelchair(rs.getDouble("wheelchair_boarding"));
            stop.setStopLat(rs.getDouble("stop_lat"));
            stop.setStopLon(rs.getDouble("stop_lon"));

            stops.add(stop);
        }
        stmt.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        logger.debug("returning " + stops.size() + " stops");
        return stops;   // may be null
    }

    public GtfsAgency getRouteAgency(String agencyId) {

        String query = "select * from agency where agency_id = '" + agencyId + "'";
        GtfsAgency agency = new GtfsAgency();

        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            // should be a single entity
            while (rs.next()) {
                agency.setAgencyId(rs.getString("agency_id"));
                agency.setAgencyName(rs.getString("agency_name"));
                agency.setAgencyTimezone(rs.getString("agency_timezone"));
                agency.setAgencyLanguage(rs.getString("agency_lang"));
            }
            stmt.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return agency;
    }
    
    /**
     * A route is a group of trips that are displayed to riders as a single service.
     */
    public List<GtfsRoute> getRoutes(int routeTypeKey) {

        String query = "select * from routes where route_type = " + routeTypeKey;
        List<GtfsRoute> routes = new ArrayList<>();

        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                GtfsRoute route = new GtfsRoute();
                // column names could be hard coded in Entities
                route.setRouteId(rs.getString("route_id"));
                route.setAgencyId(rs.getString("agency_id"));
                route.setRouteShortName(rs.getString("route_short_name"));
                route.setRouteLongName(rs.getString("route_long_name"));
                routes.add(route);
            }
            stmt.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        logger.debug("returning " + routes.size() + " routes for routeType " + routeTypeKey);
        return routes;
    }

    /**
     * The Route or 'Line' describes Transit Vehicle Types. The Key is defined
     * in GTFS but may be interpreted and used differently by different
     * agencies.
     */
    public String getRouteType(int routeTypeKey) {

        String query = "select * from route_type " + " where route_type = " + routeTypeKey;
        String description = "";

        Statement stmt = null;
        // would be cheaper to create PreparedStatement once and vary parameters
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            // should be a single record
            while (rs.next()) {
                description = rs.getString("route_type_name") + " - " + rs.getString("route_type_desc");
            }
            stmt.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return description;
    }

    /**
     * Set up database connection at construction time.
     */
    public GtfsApi(String dbUrl, String dbUser, String dbPassword) {
        connection = getConnection(dbUrl, dbUser, dbPassword);
    }

    private Connection connection;

    public Connection getConnection(String dbUrl, String dbUser, String dbPassword) {
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found?");
            return connection; // = null
        }

        try {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (SQLException e) {
            logger.error("Connection Failed! Check output console");
            return connection; // = null
        }

        return connection;
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            // e.printStackTrace();
        }
    }

    private static Log logger = LogFactory.getLog(GtfsApi.class);

    /**
     * Get weekday in a String format for a (postgres) query.
     */
    public String getWeekday(ZonedDateTime zonedDateTime) {
        String queryWeekday = "";
        switch (zonedDateTime.getDayOfWeek()) {
        case MONDAY:
            queryWeekday = "monday";
            break;
        case TUESDAY:
            queryWeekday = "tuesday";
            break;
        case WEDNESDAY:
            queryWeekday = "wednesday";
            break;
        case THURSDAY:
            queryWeekday = "thursday";
            break;
        case FRIDAY:
            queryWeekday = "friday";
            break;
        case SATURDAY:
            queryWeekday = "saturday";
            break;
        case SUNDAY:
            queryWeekday = "sunday";
            break;
        default:
            System.err.println("Couldn't determine weekday?");
            break;
        }
        return queryWeekday;
    }


}
