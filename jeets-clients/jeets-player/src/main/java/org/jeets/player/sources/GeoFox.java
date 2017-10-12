package org.jeets.player.sources;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jeets.protocol.Traccar;
import org.jeets.protocol.Traccar.Position.Builder;

import de.hvv.pojo.Coordinate;
import de.hvv.pojo.CourseElement;
import de.hvv.pojo.Departure;
import de.hvv.pojo.FilterEntry;
import de.hvv.pojo.GTITime;
import de.hvv.pojo.SDName;
import de.hvv.request.DCRequest;
import de.hvv.request.DLRequest;
import de.hvv.response.DLResponse;
import de.hvv.rest.GeoFoxCustomApi;
import de.hvv.rest.GeoFoxException;

public class GeoFox {

    public List<Builder> getLiveTrack(String lineKey, String departureString, String viaString, int departureOffset)
            throws GeoFoxException {

        GTITime time = getTimeFromNow(departureOffset);
        Departure departure = locateVehicle(lineKey, departureString, viaString, time);
        logger.info("found train departing from " + departureString + " at " 
                + time + " plus " + departure.getTimeOffset() + " minutes");
//      logger.info(departure);

        int serviceId = departure.getServiceId();
//      if (serviceId = -1) ..
//      must be exact stations, with direction = destination
        SDName origin = getStation(departure.getLine().origin);
        String destination = departure.getLine().direction;
        List<CourseElement> departureCourse =
                getDepartureCourse(serviceId, lineKey, origin, destination);
       
//      logger.info("found " + departureCourse.size() + " CourseElements");
        CourseElement firstElement = departureCourse.get(0);
//      logger.info("CourseElement#0 " + firstElement);
        String depTime = firstElement.getDepTime();  // + firstElement.getArrDelay()
        String originName = firstElement.getFromStation().getName();
//      check past ..
        System.out.println("train left origin " + originName + " at " + depTime);
//      .. or near future (restart vehicleLocation to find already moving train!?)
//      System.out.println("train will leave origin " + originName + " at " + depTime); 
        
//      prepare Track for GPS Player:
        return composeCourseTrack(departureCourse);
    }

    private List<Builder> composeCourseTrack(List<CourseElement> courseElements) {

        List<Builder> protoPositionBuilders = new ArrayList<>();
        for (CourseElement course : courseElements) {
            List<Coordinate> path = course.getPath().track;
            // calculate complete (pythagoras!) distance via path
            double courseLength = 0d;
            double[] courseElement = new double[path.size() - 1];
            for (int i = 0; i < path.size() - 1; i++) {
                double horizontal = path.get(i).getX() - path.get(i + 1).getX();
                double vertical   = path.get(i).getY() - path.get(i + 1).getY();
                courseElement[i] = Math.sqrt(Math.pow(horizontal, 2) + Math.sqrt(Math.pow(vertical, 2)));
                courseLength += courseElement[i];
            }
            ZonedDateTime depDateTime = ZonedDateTime.parse(course.getDepTime(), geofoxDateTime);
            ZonedDateTime arrDateTime = ZonedDateTime.parse(course.getArrTime(), geofoxDateTime);
            // output of stops for GPS Player:
//            System.out.println( "from: " + course.getFromStation().getCoordinate() + " " 
//                    + depDateTime.format(outputDateTime) + " " + course.getFromStation().getName() );
            Coordinate toStationCoordinate = course.getToStation().getCoordinate();
//            System.out.println( " to: " + toStationCoordinate + " " 
//                    + arrDateTime.format(outputDateTime) + " " + course.getToStation().getName() );
//          subtract x seconds stop time from arrDate to simulate stop
            double stopTime = 10.0d;    // train stops for [stopTime] seconds
            double duration = Duration.between(depDateTime, arrDateTime).getSeconds() - stopTime;
            double durationMs = duration * 1000;
//          logger.info("courseLength: " + courseLength + " in " + duration + " seconds " + durationMs);

            double percent, deltaMs;
            SimpleDateFormat sdfDate = new SimpleDateFormat("dd. MMM HH:mm:ss.SSS");
            Date depDate = null;
//          logger.info("path.size: " + path.size() );
            for (int i = 0; i < path.size(); i++) {
                Coordinate coordinate = path.get(i);
                Builder positionBuilder = Traccar.Position.newBuilder();
                positionBuilder.setLongitude(coordinate.getX());
                positionBuilder.setLatitude(coordinate.getY());
                if (i == 0) { // first
                    depDate = new Date(depDateTime.toInstant().toEpochMilli());
                    // output for GPS Player: (use println to avoid logger overhead)
                    System.out.println(coordinate + "\t at " + depDateTime.format(outputDateTime)
                            + "\tstart " + course.getFromStation().getName());
//                  TODO: add STATION_START event and attribute stationName = ...
                } else if (i < path.size()) {
                    percent = courseElement[i - 1] / courseLength;
                    deltaMs = percent * durationMs;
                    depDate = new Date(depDate.getTime() + (long) deltaMs);
                    // output for GPS Player:
                    if ((coordinate.getX() == toStationCoordinate.getX())
                            && (coordinate.getY() == toStationCoordinate.getY()))
                        System.out.println(coordinate + "\t at " + sdfDate.format( depDate ) 
                        + "\tstop  " + course.getToStation().getName());
//                      TODO: add STATION_STOP event and attribute stationName = ...
//                  else
//                      System.out.println(coordinate + "\t at " + sdfDate.format( depDate ));
                        // logger.info(coordinate + "\t" + deltaMs + " ms at " + depDate );
                }
                positionBuilder.setFixtime(depDate.getTime());
                protoPositionBuilders.add(positionBuilder);
            }
//          logger.info( course.getToStation().getCoordinate() + " " + course.getArrTime());
        }
        return protoPositionBuilders;
    }

    private List<CourseElement> getDepartureCourse(int serviceId, String lineKey, SDName station, String destinationString)
            throws GeoFoxException {
        DCRequest dcRequest = new DCRequest();
        dcRequest.setServiceId(serviceId);
        dcRequest.setLineKey(lineKey);
        dcRequest.setStation(station);
        dcRequest.setShowPath(true);
        // these two are required, but overridden by serviceId
        dcRequest.setTime(sdfDate.format(new Date()));
        dcRequest.setDirection(destinationString);
        return geofox.departureCourse(dcRequest);
    }

    /**
     * Determine a (any) vehicle from LineKey (i.e.U1) currently traveling 
     * in the direction defined by any departure and any direction Station.
     * 
     * Hint: Pick a Service and a Station roughly in the middle, preferably down town. 
     * 
     * Look up complete traveling time, divide by two, add to 'now' and 
     * use as maxTimeOffset. To receive all transit vehicles
     * currently moving. Then pick one by latest time to destination, find out
     * where it is and feed the rest of the route to the GPS Player!
     * 
     * Developer Note: There should always be a traveling U1 during daytime
     * (5-0). Traveling time Jungfernstieg - Norderstedt ca. 36 mins. 
     * For development at night time chose a night bus or similar.
     */
    public Departure locateVehicle(String lineKey, String departure, String direction, GTITime time) throws GeoFoxException
    {
        SDName depStation = getStation(departure);
//      direction filter
        FilterEntry entry = new FilterEntry();
        entry.setServiceID(lineKey);
        SDName directionStation = getStation(direction);
        List<String> stations = new ArrayList<String>();
        stations.add(directionStation.getId());
        entry.setStationIDs(stations);
        List<FilterEntry> filter = new ArrayList<FilterEntry>();
        filter.add(entry);

        int maxTimeOffset = 30;                 // hard coded
        DLRequest dlRequest = new DLRequest();
        dlRequest.setStation(depStation);       // departure
        dlRequest.setFilter(filter);            // direction
        dlRequest.setTime(time);
        dlRequest.setMaxList(5);
        dlRequest.setMaxTimeOffset(maxTimeOffset);  // required and not 0!
        dlRequest.setAllStationsInChangingNode(false);

        DLResponse dlFilteredResponse = geofox.departureList(dlRequest);
//      logger.info(dlFilteredResponse.toString());
        List<Departure> departures = dlFilteredResponse.getDepartures();
        if ((departures == null) || (departures.size() > 0)) {
            return dlFilteredResponse.getDepartures().get(0);
        }
        // this can happen at night when no trains are available
        else {
            logger.warn("No trains leaving from " + depStation.getName() 
            + " for line " + lineKey 
            + " at " + time.date + " " + time.time 
            + " for " + maxTimeOffset + " minutes.");
            return null;
        }

    }

    /**
     * Get a valid Station object.
     */
    private SDName getStation(String stationName) throws GeoFoxException {
        System.out.println("getStation: " + stationName);
        SDName theName = new SDName();
        theName.setType(SDName.SDType.STATION);
        theName.setName(stationName);
        List<SDName> stations = geofox.getStations(theName);
        if (stations.size()>1)
            logger.warn("More than one '" + theName + "' stations found.\n Returning first.");;
//      check null
        return stations.get(0);
    }

    /**
     * Get Time in (minutes) from now. Use negative minutes to go back in time.
     * 
     * @return GTITime object
     */
    public GTITime getTimeFromNow(int minutes) {

        GTITime gtiTime = new GTITime();
        if (minutes == 0)
            return gtiTime;

//      TODO: time zone "Europe/Berlin"
        LocalDateTime currentTime = LocalDateTime.now();
        currentTime = currentTime.plusMinutes(minutes);
//      currentTime = currentTime.minusMinutes(minutes);

        LocalDate date = currentTime.toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = date.format(formatter);
        gtiTime.date = formattedDate;

        LocalTime time = currentTime.toLocalTime();
        formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = time.format(formatter);
        // logger.info("gtiTime.time = " + formattedTime);
        gtiTime.time = formattedTime;

        return gtiTime;
    }

    private String geofoxDateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private SimpleDateFormat sdfDate = new SimpleDateFormat(geofoxDateTimeFormat);
    private DateTimeFormatter geofoxDateTime = DateTimeFormatter.ofPattern(geofoxDateTimeFormat);
    private DateTimeFormatter outputDateTime = DateTimeFormatter.ofPattern("dd. MMM HH:mm:ss.SSS");

    private static Log logger = LogFactory.getLog(GeoFox.class);
    private GeoFoxCustomApi geofox = new GeoFoxCustomApi("beiglb","I@/0(tGf=S%B");

}