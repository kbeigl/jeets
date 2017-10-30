package org.jeets.playback.gtfs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jeets.model.gtfs.GtfsTrip;

/**
 * This is a generalized GTFS Factory (General Transit Feed Specification) to
 * create any transit vehicle from GTFS deliveries.
 * 
 * The GeoFox Factory is also based on GTFS, although it is accessed via REST
 * service interface. It should be used as the template for generalization and
 * to create standard interfaces for Transit Vehicles.
 *
 * @author kbeigl@jeets.org
 */
@Deprecated
public class GtfsFactoryWithViews {
    
//  TODO: extract Gtfs API, Interface and apply on top of GeoFox REST API
//  TODO: create Gtfs Persistence Unit a rewrite factory with Gtfs Entities
//        create Foreign Keys in database before generating PU

    private String host = "127.0.0.1";
    private int port = 5432;
    private String dbName = "HVV-20171006";
    public String dbUser = "postgres";
    public String dbPassword = "postgres";
    public String dbUrl = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
//  "jdbc:postgresql://127.0.0.1:5432/HVV-20171006", "postgres", "postgres");
    
    /**
     * For convenience set up default connection at construction time.
     */
    public GtfsFactoryWithViews() {
        connection = getConnection(dbUrl, dbUser, dbPassword);
    }

    /*
     * Currently some methods create VIEWs in the database instead of returning
     * a Java Collection. These return types could be introduced to verify that
     * intermediate steps return results at all. Instead of SQL (in DBMS)
     * 'Collection Algebra' (in RAM) could be used to find results.
     */
    public void getNextTrip(String route, String fromStation, String toStation, Date startTime) {
        try {
            Instant startAt = startTime.toInstant();
//          TODO: retrieve from table agency (group by!) agency_timezone
            ZoneId currentZone = ZoneId.of("Europe/Berlin");
            ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(startAt, currentZone);

            String queryDay = DateTimeFormatter.ofPattern("yyyyMMdd").format(zonedDateTime);
            String queryWeekday = getWeekday(zonedDateTime);
            logger.info("Query date (weekday): " + queryDay + " (" + queryWeekday + ")");
//          prepare = CREATE OR REPLACE VIEW ..
            prepareServicesForDay(queryDay, queryWeekday);

            prepareFromStations(fromStation);
            prepareToStations(toStation);
            String routeId = getRouteId(route);
            prepareRouteTripsForDay(routeId);

//          all VIEWs are updated for main query at this point > create ...
            String queryTime = DateTimeFormatter.ofPattern("HH:mm:ss").format(zonedDateTime);
//          TODO: DateTimeFormatter returns 24h format 
//              -> in GTFS values >24 are allowed for next day after midnight ...
            GtfsTrip trip =  getNextTrip(queryTime);
            logger.info("Found trip_id " + trip.getTripId() + " to " + trip.getHeadsign() + 
                    " with shape_id " + trip.getShapeId() + " / service_id: " + trip.getServiceId());
            logger.info("departing from " + trip.getDepStop() + " (" + trip.getDepSeq() + ") at " + trip.getDepTime());
            logger.info(" arriving at   " + trip.getArrStop() + " (" + trip.getArrSeq() + ") at " + trip.getArrTime());
            
//            HIER GEHTS WEITER
//          get shape - sync with stations/times - add stop/arr start/dep events !!

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /*
     * Find trip from previously created views for services, from-, toStation,
     * routeTrips
     */
    private GtfsTrip getNextTrip(String departureTime) throws SQLException {
        
//      define return collection type with useful values (lat lon ...)
        
        String tripQuery = "SELECT "
//              + "t.trip_id, t.shape_id, t.trip_headsign, "
                + "t.trip_id, t.route_id, t.service_id, t.shape_id, t.trip_headsign, "
//              departure
                + "st1.stop_id dep_stop_id, st1.stop_sequence dep_seq, st1.departure_time dep_time, "
//              arrival
                + "st2.stop_id arr_stop_id, st2.stop_sequence arr_seq, st2.arrival_time arr_time "
                + " FROM route_trips t, stop_times st1, stop_times st2 "
                + "WHERE st1.trip_id = t.trip_id "
                + "  AND st2.trip_id = t.trip_id "
                + "  AND st1.stop_id  IN (select stop_id from from_stations) "
                + "  AND st2.stop_id  IN (select stop_id from to_stations) "
                + "  AND t.service_id IN (select service_id from services_today) "
                + "  AND st1.departure_time >= '" + departureTime + "' "
//              + "  AND st1.departure_time <= '14:10:00' "
//              determine duration (?)
//              + "  AND st2.arrival_time >= '14:40:00' "
//              + "  AND st2.arrival_time <= '14:50:00' "
                + "  AND st1.pickup_type = 0 "
                + "  AND st2.drop_off_type = 0 "
                + "  AND st1.departure_time < st2.arrival_time "
                + "ORDER BY st1.departure_time "
                + "LIMIT 1";    // always return a single dataset
        
        Statement stmt = null;
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(tripQuery);
        GtfsTrip trip = new GtfsTrip();
//      should be a single record (handle without next?)
        while (rs.next()) {
//          TODO: apply column names from above query:            
//          t.trip_id, t.route_id, t.service_id, t.shape_id, t.trip_headsign ..
            trip.setTripId(rs.getString(1));
            trip.setRouteId(rs.getString(2));
            trip.setServiceId(rs.getString(3));
            trip.setShapeId(rs.getString(4));
            trip.setHeadsign(rs.getString(5));
//          departure
            trip.setDepStop(rs.getString(6));
            trip.setDepSeq(rs.getInt(7));
            trip.setDepTime(rs.getString(8));
//          arrival
            trip.setArrStop(rs.getString(9));
            trip.setArrSeq(rs.getInt(10));
            trip.setArrTime(rs.getString(11));
        }
        stmt.close();
        return trip;
    }

    private void prepareRouteTripsForDay(String routeId) throws SQLException {
        String routeTripsQuery = "create view route_trips as ("
                + "select tr.* "
                + "  from services_today st, trips tr "
                + " where st.service_id = tr.service_id "
                + "   and tr.route_id = '" + routeId + "'"
                + ")";
        
        Statement stmt = null;
        stmt = connection.createStatement();
        stmt.executeUpdate("DROP VIEW IF EXISTS route_trips");
        stmt.close();

        stmt = connection.createStatement();
        stmt.executeUpdate(routeTripsQuery);
        stmt.close();
    }

    private void prepareToStations(String toStation) throws SQLException {
        String fromStationQuery = "create view to_stations as ("
//              id, name, lat, lon .. 
                + "select * from stops "
                + "where stop_name = '" + toStation + "' "
//              0 or blank = stop, 1 = station
                + "and location_type = 0"
                + ")";
        
        Statement stmt = null;
        stmt = connection.createStatement();
        stmt.executeUpdate("DROP VIEW IF EXISTS to_stations");
        stmt.close();

        stmt = connection.createStatement();
        stmt.executeUpdate(fromStationQuery);
        stmt.close();
    }

    private void prepareFromStations(String fromStation) throws SQLException {
        String fromStationQuery = "create view from_stations as ("
                + "select * from stops "
                + "where stop_name = '" + fromStation + "' "
//              0 or blank = stop, 1 = station
                + "and location_type = 0"
                + ")";
        
        Statement stmt = null;
        stmt = connection.createStatement();
        stmt.executeUpdate("DROP VIEW IF EXISTS from_stations");
        stmt.close();

        stmt = connection.createStatement();
        stmt.executeUpdate(fromStationQuery);
        stmt.close();
    }

    /**
     * Instead of retrieving a Collection a view is created for subsequent
     * queries!
     */
    private void prepareServicesForDay(String serviceDay, String serviceWeekday ) throws SQLException {
        
//              1. main set of service IDs for the day
//              2. EXCEPT service IDs to be excluded  
//              3. UNION  service IDs to be added
                String serviceQuery = "create view services_today as ("
                        + "SELECT service_id FROM calendar"
                        + " WHERE start_date <='" + serviceDay + "'"
                        + "   AND end_date   >='" + serviceDay + "'"
                        + "   AND " + serviceWeekday + " = true"
                        + "       UNION "
                        + "SELECT service_id FROM calendar_dates"
                        + " WHERE date='" + serviceDay + "' AND exception_type = 2"
                        + "       EXCEPT "
                        + "SELECT service_id FROM calendar_dates"
                        + " WHERE date='" + serviceDay + "' AND exception_type = 1"
                        + ")";
                
                Statement stmt = null;
                stmt = connection.createStatement();
                stmt.executeUpdate("DROP VIEW IF EXISTS services_today CASCADE");
                stmt.close();

//              would be cheaper to create PreparedStatement once and vary parameters
                stmt = connection.createStatement();
                stmt.executeUpdate(serviceQuery);
                stmt.close();
                
//              execute new query to verify results > 0 ? return true ?
    }

    /**
     * Get weekday in a String format for a (postgres) query.
     */
    private String getWeekday(ZonedDateTime zonedDateTime) {
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

    /**
     * A route is a group of trips that are displayed to riders as a single service.
     */
    public String getRouteId(String routeShortName) throws SQLException {
        String query = "select * from routes where route_short_name = '" + routeShortName + "'";
        String routeId = "";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                routeId = rs.getString(1);
                String routeLongName = rs.getString(4);
//              TODO: analyze geometry (last field) > IS IT FULL (for all trips) ?!!
                logger.info("Found route(" + routeId + "): " + routeLongName);
            }
        } catch (SQLException e ) {
            throw e;
        } 
        return routeId;
    }

    private Connection connection;

    public Connection getConnection(String dbUrl, String dbUser, String dbPassword) {
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found?");
            return connection;      // = null
        }

        try {
            connection = DriverManager.getConnection( dbUrl, dbUser, dbPassword );
        } catch (SQLException e) {
            System.err.println("Connection Failed! Check output console");
            return connection;      // = null
        }

        return connection;
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
//          e.printStackTrace();
        }
    }


    private static Log logger = LogFactory.getLog(GtfsFactoryWithViews.class);

}
