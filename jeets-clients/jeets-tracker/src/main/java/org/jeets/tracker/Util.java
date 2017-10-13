package org.jeets.tracker;

import java.util.Date;

import org.jeets.model.traccar.jpa.Position;

public class Util {

    /**
     * Create single GPS position message:
     * "imei:359587010124900,tracker,809231329,13554900601,F,132909.397,A,2234.4669,N,11354.3287,E,0.11,"
     * 
     * @param position
     * @return
     */
    private static String convertPositionEntityToGps103Message(Position position) {
        // position.getDevices().getId(); don't use
        // empty relation?
        // String uniquId = position.getDevices().getUniqueid();
        // StringBuilder message = new StringBuilder("imei:" + uniquId + ",");
        StringBuilder message = new StringBuilder("imei:359587010124999,"); 
        // message.append("tracker,"); // regular message
        // message.append("help me,"); // alarm message
        message.append(","); // empty message

        // message.append("0809231929,"); // yymmdd
        // localHours=19,localMinutes=29
        // position.getFixtime(); // not applied ?

        Date devTime = position.getDevicetime();
        int y = devTime.getYear() - 100; // 2008 - 1900 - 100 = 8 > one digit
        if (y < 10)
            message.append("0" + y); // one
        else
            message.append(y); // two digits
        int m = devTime.getMonth() + 1;
        if (m < 10)
            message.append("0" + m); // one
        else
            message.append(m); // two digits
        int d = devTime.getDate();
        if (d < 10)
            message.append("0" + d); // one
        else
            message.append(d); // two digits
        int tz = devTime.getTimezoneOffset(); // ignored
        // devTime.toGMTString();
        int h = devTime.getHours();
        if (h < 10)
            message.append("0" + h); // one
        else
            message.append(h); // two digits
        m = devTime.getMinutes();
        if (m < 10)
            message.append("0" + m); // one
        else
            message.append(m); // two digits
        message.append(",");

        message.append("13554900601,F,"); // phone#, rfid ?

        // message.append("112909.397"); // hhmmss.millis
        h = devTime.getHours();
        if (h < 10)
            message.append("0" + h); // one
        else
            message.append(h); // two digits
        m = devTime.getMinutes();
        if (m < 10)
            message.append("0" + m); // one
        else
            message.append(m); // two digits
        int s = devTime.getSeconds();
        if (s < 10)
            message.append("0" + s); // one
        else
            message.append(s); // two digits
        message.append(".");
        long ms = (devTime.getTime());
        String millis = (ms + "");
        millis = millis.substring(millis.length() - 3);
        message.append(millis);
        message.append(",A,"); // A = valid

        // message.append("2234.4669,N,11354.3287,E,0.11,");
        double latDec = position.getLatitude();
        message.append(convertWGS84to_dddmm_mmmm(latDec) + ",");
        String latDir = "S";
        if (latDec > 0)
            latDir = "N";
        message.append(latDir + ",");

        double lonDec = position.getLongitude();
        message.append(convertWGS84to_dddmm_mmmm(lonDec) + ",");
        String lonDir = "W";
        if (lonDec > 0)
            lonDir = "E";
        message.append(lonDir + ",");

        message.append("0.11,"); // distance
        return message.toString();
    }

    /**
     * Convert WGS84 latitude (49.123) or longitude (12.456) to (d)ddmm.mmmm
     * String format to degrees and minutes 4907.37999, 01227.36000
     * <p>
     * Note that the direction (+/- N/S W/E) is lost in this conversion
     * and should be derived externally from the wgs84 sign.
     */
    public static String convertWGS84to_dddmm_mmmm(double wgs84) {
        wgs84 = Math.abs(wgs84);
        int fullDegrees =(int) wgs84;
        StringBuilder message = new StringBuilder(fullDegrees + "");
        double mm = wgs84 - fullDegrees;
        double minutes = mm * 60;
        if (minutes < 10)
            message.append("0");
     // add trailing 0000 for divisions with less than 4 fractions
        message.append(minutes + "0000");
        String msgFormat = message.substring(0, 10);  // dd
        if (fullDegrees <= 100)
            msgFormat = message.substring(0, 9);     // ddd
//      message.append(msgFormat);
//      return message.toString();
        return msgFormat;
    }
    
//    below methods have been replaced with protocol.Samples
//    to be removed

//    public static Traccar.Device createDeviceMessage() {
//        Traccar.Device.Builder deviceBuilder = Traccar.Device.newBuilder();
//        deviceBuilder.setUniqueid("395389");
//        deviceBuilder.addPosition(createProtoPositionBuilder(49.12d, 12.34d));
//        deviceBuilder.addPosition(createProtoPositionBuilder(49.56d, 12.78d));
//        return deviceBuilder.build();
//    }

//    public static Traccar.Position.Builder createProtoPositionBuilder(double latitude, double longitude) {
//        Traccar.Position.Builder positionBuilder = Traccar.Position.newBuilder();
//        positionBuilder.setDevicetime(new Date().getTime()); // 16:31:36 no millis ?
//        positionBuilder.setFixtime(new Date().getTime()); // 2016-10-10 16:31:36
//        // positionBuilder.setValid(true);
//        positionBuilder.setLatitude(latitude);
//        positionBuilder.setLongitude(longitude);
//        positionBuilder.setAltitude(333.111d); // precision ?
//        positionBuilder.setAccuracy(0.345d);
//        positionBuilder.setSpeed(12.345d);
//        positionBuilder.setCourse(100.123d);
//        // "{\"battery\":\"44.0\",\"ip\":\"87.160.15.61\",\"distance\":0.0,\"totalDistance\":0.0}"
//        // positionBuilder.setAttributes(position.getAttributes());
//        positionBuilder.addEvent(createProtoEvent());
//        return positionBuilder;
//    }

//    private static Traccar.Event createProtoEvent() {
//        Traccar.Event.Builder eventBuilder = Traccar.Event.newBuilder();
//        eventBuilder.setEvent(Traccar.EventType.KEY_ALARM);
//        eventBuilder.setAlarm(Traccar.AlarmType.ALARM_SOS);
//        return eventBuilder.build();
//    }

}
