package org.jeets.tracker;

import java.util.Date;

import org.jeets.protocol.Traccar;
import org.jeets.protocol.util.Samples;

/**
 * This class is for stand alone tests of the JeeTS Tracker. 
 * Use Maven to compile a 'jar-with-dependencies' and run with: <br>
 * java -jar jeets-tracker-x.y-jar-with-dependencies.jar  <br>
 * to send a 'ping' to the Tracking (or Device Communication) Server.
 * 
 * @author kbeigl@jeets.org
 */
public class Main {

    public static void main(String[] args) {
        String host = "127.0.0.1";  // "localhost" default
        int port = 5001;            //  traccar default gps103.port
        String messageString =      //  valid gps103 sample
                "imei:359587010124999,help me,1201011201,," +
                "F,120100.000,A,6000.0000,N,13000.0000,E,0.00,;";  
        
        if (args.length > 1) {
            host = args[0];
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("port" + args[1] + " must be an integer." + usage());
                System.exit(1);
            }
        }
        else
            throw new IllegalArgumentException("\nInvalid number of arguments: " + usage());

        if (args.length == 2) {
//          Traccar.Device deviceOrm = Util.createDeviceMessage();
//          TODO: args
            Tracker tracker = new Tracker(host, port, "pb.device");
//          Traccar.Position.Builder pos = Util.createProtoPositionBuilder(49.05d, 12.15d);
//            Traccar.Device.Builder deviceBuilder = Samples.createDeviceWithPositionWithOneEvent();
            Traccar.Position.Builder positionBuilder = Samples.createPositionProto();
            Traccar.Event.Builder eventBuilder = Samples.createAlarmEventProto();
            positionBuilder.addEvent(eventBuilder);
//          Traccar.Position positionProto = positionBuilder.build();
            System.out.println("transmitPositionProto: " + host + ":" + port + " at " + new Date().getTime()
                    + "\n" + positionBuilder);
//                  + "\n" + deviceBuilder);

//          for (int i = 0; i < 5; i++) {   // performance test
//              tracker.transmitTraccarDevice(deviceBuilder.build());
                tracker.transmitPositionProto(positionBuilder);
//              TODO change signature (?)
//              tracker.fireProtoPosition(positionBuilder.build());
//          }
//          TODO old pattern with static methods - change with test cases
//          tracker.transmitTraccarPosition(Util.createProtoPosition(49.05d, 12.15d));
//          tracker.transmitTraccarDevice(deviceOrm);
        }
        else if (args.length == 3) {
            messageString = args[2];
            System.out.println("transmitMessage: " + host + ":" + port + " \"" + messageString + "\"");
            Tracker.transmitProtocolString(messageString, host, port);
        }
        else
            throw new IllegalArgumentException("\nInvalid number of arguments: " + usage());
    }
    
    private static String usage() {
        return "\n host port \"messageString\"" 
             + "\n host port (send fixed Protobuffer Message)";
    }

}
