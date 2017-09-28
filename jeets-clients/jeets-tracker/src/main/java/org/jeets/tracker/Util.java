package org.jeets.tracker;

public class Util {

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
