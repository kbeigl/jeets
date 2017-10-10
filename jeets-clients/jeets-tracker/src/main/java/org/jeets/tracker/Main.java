package org.jeets.tracker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jeets.protocol.Traccar;
import org.jeets.protocol.Traccar.Position.Builder;
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

//  mvn exec:java -Dexec.mainClass="org.jeets.tracker.Main" -Dexec.args="'127.0.0.1' 5200"

    public static void main(String[] args) {
        String host = "127.0.0.1";  // "localhost" default
        int port = 5001;            //  traccar default gps103.port
        String messageString =      //  valid gps103 sample
                "imei:359587010124999,help me,1710021201,," +
                "F,120100.000,A,4900.0000,N,1200.0000,E,0.00,;";  
        
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
            Tracker tracker = new Tracker(host, port, "pb.device");
            List<Traccar.Position.Builder> posBuilderList = createSampleTrack();
            System.out.println("created list with " + posBuilderList.size() + " positions.");
            for (int pos = 0; pos < posBuilderList.size(); pos++) {
                Traccar.Position.Builder posBuilder = posBuilderList.get(pos);
//              Traccar.Event.Builder eventBuilder = Samples.createAlarmEventProto();
//              posBuilder.addEvent(eventBuilder);
                posBuilder.setFixtime(new Date().getTime());
                System.out.println("fixed " + (pos+1) + ". position at " + posBuilder.getFixtime());
                tracker.sendPositionProto(posBuilder);
//              simulate 10 second interval
                try { Thread.sleep(10000);
                } catch (InterruptedException e) {}
            }
            System.out.println("All device messages posted");
        }
        else if (args.length == 3) {
            messageString = args[2];
            System.out.println("transmitMessage: " + host + ":" + port + " \"" + messageString + "\"");
            Tracker.transmitProtocolString(messageString, host, port);
        }
        else
            throw new IllegalArgumentException("\nInvalid number of arguments: " + usage());
    }
    
    private static List<Builder> createSampleTrack() {
//      actual trace with real fixtimes
//      "2017-05-20 15:49:01";49.03097993;12.10312854;407
//      "2017-05-20 15:52:57";49.02847401;12.10734587;370
//      "2017-05-20 15:54:57";49.02865676;12.11003339;383
//      "2017-05-20 15:56:58";49.03296471;12.11323104;381
//      "2017-05-20 15:58:58";49.03363147;12.12226451;392
//      "2017-05-20 16:02:55";49.03797380;12.13681046;388
        List<Traccar.Position.Builder> positions = new ArrayList<>();

        Traccar.Position.Builder positionBuilder = Traccar.Position.newBuilder();
        positionBuilder.setValid(true)
        .setLatitude(49.03097993d).setLongitude(12.10312854d).setAltitude(407d);
        positions.add(positionBuilder);

        positionBuilder = Traccar.Position.newBuilder();
        positionBuilder.setValid(true)
        .setLatitude(49.02847401d).setLongitude(12.10734587d).setAltitude(370d);
        Traccar.Event.Builder eventBuilder = Samples.createAlarmEventProto();
        positionBuilder.addEvent(eventBuilder);
        positions.add(positionBuilder);

        positionBuilder = Traccar.Position.newBuilder();
        positionBuilder.setValid(true)
        .setLatitude(49.02865676d).setLongitude(12.11003339d).setAltitude(383d);
        positions.add(positionBuilder);

        positionBuilder = Traccar.Position.newBuilder();
        positionBuilder.setValid(true)
        .setLatitude(49.03296471d).setLongitude(12.11323104d).setAltitude(381d);
        positions.add(positionBuilder);

        positionBuilder = Traccar.Position.newBuilder();
        positionBuilder.setValid(true)
        .setLatitude(49.03363147d).setLongitude(12.12226451d).setAltitude(392d);
        positions.add(positionBuilder);

        positionBuilder = Traccar.Position.newBuilder();
        positionBuilder.setValid(true)
        .setLatitude(49.03797380d).setLongitude(12.13681046d).setAltitude(388d);
        positions.add(positionBuilder);

        return positions;
    }

    private static String usage() {
        return "\n host port \"messageString\"" 
             + "\n host port (send fixed Protobuffer Message)";
    }

}
