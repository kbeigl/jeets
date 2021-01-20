package org.jeets.tracker;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.jeets.protobuf.Jeets.Position.Builder;
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
    private static int port;
    private static String messageString;
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
            Tracker tracker = new Tracker(host, port, uniqueId);
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
        List<Builder> posBuilderList = Samples.createSampleTrack();
        System.out.println("created list with " + posBuilderList.size() + " positions.");
        for (int pos = 0; pos < posBuilderList.size(); pos++) {
            Builder posBuilder = posBuilderList.get(pos);
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

    public static int getSendInMillis() {
        return sendInMillis;
    }

    public static void setSendInMillis(int sendInMillis) {
        Main.sendInMillis = sendInMillis;
    }

}
