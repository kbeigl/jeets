package org.jeets.tracker;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.jeets.protocol.Traccar;
import org.jeets.protocol.Traccar.Position.Builder;
import org.jeets.protocol.util.Samples;

/**
 * This class is for stand alone tests with the JeeTS Tracker. 
 * Use Maven to compile a 'jar-with-dependencies' and run with: <br>
 * java -jar jeets-tracker-x.y-jar-with-dependencies.jar  <br>
 * to send a 'ping' to the Tracking (or Device Communication) Server.
 * 
 * This Main class is actually a wrapper to set up the Tracker class
 * with parameters. It should be perceived as a demonstration 
 * how to apply the Tracker as a component inside your client software.
 * 
 * @author kbeigl@jeets.org
 */
public class Main {

    private static String uniqueId = "pb.device";
    private static String host = "127.0.0.1";  // "localhost" default
    private static int port = 5001;            //  traccar default gps103.port
    private static String messageString =      //  valid gps103 sample
            "imei:359587010124999,help me,1710021201,," +
            "F,120100.000,A,4900.0000,N,1200.0000,E,0.00,;";  
    private static int sendInMillis = 10000;
    
    public static void main(String[] args) {

        if ((args.length == 0) || ((args.length == 1) && (args[0].equals("-h")))) {
                System.out.println(usage());
                return;
        }
        else if ((args.length == 1) && (args[0].equals("-props"))) {
//          TODO: add -props=test.properties to load file (on classpath?)
            String propFile = "tracker.properties";
            loadPropsToSystem(propFile);
            System.out.println("run tracker with default properties: ");

            uniqueId = System.getProperty("tracker.uniqueId", uniqueId);
            host = System.getProperty("tracker.host", host);
            port = Integer.getInteger("tracker.port", port);
            int maxPosPerMsg  = Integer.getInteger("tracker.maxPosPerMsg", 2);
            int retryInMillis = Integer.getInteger("tracker.retryInMillis", 10000);
            int  sendInMillis = Integer.getInteger("tracker.sendInMillis", 10000);

//          System.out.println(" project.name: " + prop.getProperty("project.name"));   // maven variable
//          System.out.println("what.ever: " + prop.getProperty("what.ever"));          // test null
            System.out.println("\t\t         host: " + host);
            System.out.println("\t\t         port: " + port);
            System.out.println("\t\t     uniqueId: " + uniqueId);
            System.out.println("\t\t maxPosPerMsg: " + maxPosPerMsg);
            System.out.println("\t\tretryInMillis: " + retryInMillis);
            System.out.println("\t\t sendInMillis: " + sendInMillis);

            Tracker tracker = new Tracker(host, port, uniqueId);
            tracker.setMaxPosPerMsg(maxPosPerMsg);
            tracker.setRetryInMillis(retryInMillis);
            setSendInMillis(sendInMillis);
            
            sendSampleMessages(tracker);

        }
        else if (args.length > 1) {
            host = args[0]; //  first argument must be the host
            try {           // second argument must be the port
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("port" + args[1] + " must be an integer." + usage());
                System.exit(1);
            }
        }

        if (args.length == 2) {
            Tracker tracker = new Tracker(host, port, "pb.device");
            sendSampleMessages(tracker);
        }
        else if (args.length == 3) {
            messageString = args[2];
            System.out.println("transmitMessage: " + host + ":" + port + " \"" + messageString + "\"");
            Tracker.transmitProtocolString(messageString, host, port);  // static
        }
        else
            throw new IllegalArgumentException("\nInvalid number of arguments: " + usage());
    }

    private static void sendSampleMessages(Tracker tracker) {
        List<Traccar.Position.Builder> posBuilderList = createSampleTrack();
        System.out.println("created list with " + posBuilderList.size() + " positions.");
        for (int pos = 0; pos < posBuilderList.size(); pos++) {
            Traccar.Position.Builder posBuilder = posBuilderList.get(pos);
//              Traccar.Event.Builder eventBuilder = Samples.createAlarmEventProto();
//              posBuilder.addEvent(eventBuilder);
            posBuilder.setFixtime(new Date().getTime());
            System.out.println("fixed " + (pos+1) + ". position at " + posBuilder.getFixtime());
            tracker.sendPositionProto(posBuilder);
            try { Thread.sleep(sendInMillis);
            } catch (InterruptedException e) {}
        }
        System.out.println("All device messages were sent and acknowledged");
    }

    /**
     * Set all props from the file to System.setProperty(key, val). 
     * @param filename
     */
    private static void loadPropsToSystem(String filename) {
        Properties props = new Properties();
        InputStream input = null;
        try {
//          input = new FileInputStream(filename);  // on classpath
            input = Main.class.getClassLoader().getResourceAsStream(filename);
            if(input==null){
                System.err.println(filename + " wasn't found!");
            }
            props.load(input);

            System.setProperty("tracker.host", props.getProperty("tracker.host"));
            System.setProperty("tracker.port", props.getProperty("tracker.port"));
            System.setProperty("tracker.uniqueId", props.getProperty("tracker.uniqueId"));
            System.setProperty("tracker.maxPosPerMsg", props.getProperty("tracker.maxPosPerMsg"));
            System.setProperty("tracker.retryInMillis", props.getProperty("tracker.retryInMillis"));
            System.setProperty("tracker.sendInMillis", props.getProperty("tracker.sendInMillis"));

        } catch (IOException ex) {
            System.err.println(filename + " wasn't loaded. Stop application.");
//          ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    System.err.println(filename + " wasn't closed.");
//                  e.printStackTrace();
                }
            }
        }
    }

    private static String usage() {
//      TODO: regression tracker2dcs itest
        return "usage of jeets-tracker.jar"
                + "\n <no args>       - this help screen"
                + "\n -h              - this help screen"
                + "\n -props          - use default properties file (inside jar) to"
                + "\n                   send sample Protobuffer Messages"
                + "\n host port       - send sample Protobuffer Messages" 
                + "\n host port \"messageString\" - send any String message (for testing)" ;
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

    public static int getSendInMillis() {
        return sendInMillis;
    }

    public static void setSendInMillis(int sendInMillis) {
        Main.sendInMillis = sendInMillis;
    }

}
