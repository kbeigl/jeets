package org.jeets.tracker;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import org.jeets.tracker.netty.TraccarSender;
import org.jeets.model.traccar.jpa.Position;
import org.jeets.protocol.Traccar;
import org.jeets.protocol.Traccar.Device.Builder;
import org.jeets.protocol.util.Transformer;

/**
 * The JeeTS Tracker sends Protobuffer Messages, i.e. POJOs defined in the
 * *.proto file. External clients can only add Positions while the Tracker
 * internally adds Device with uniqueId and potential Tracker Events!
 * <p>
 * Every Tracker instance has a uniqueId and a List<Traccar.Position> as internal database.
 * The Tracker can be configured to send these positions to host and port as needed and as connectivity allows.
 * The Client (like the Player) submits the Traccar.Position in real (playback) time.
 * The Tracker can be configured to send a number of Positions at a time,
 * to send by a time interval (flush database). Accordingly the Tracker can clean up
 * the List after successful ACK or retry if not successful.
 * 
 * @author kbeigl@jeets.org
 */
//  TODO: add NioEventLoopGroup management instance for complete runtime
//  tracker.transmitTraccarPosition(position)
public class Tracker {

    private String uniqueId = "pb.device";
    private String host = "localhost";
    private int port = 5200;
    private boolean messageLoopRunning = false;

    /**
     * The Tracker is created with host, port and uniqueId and will send
     * positions as long as the queue is not empty.
     */
    public Tracker( String host, int port, String uniqueId ) {
        this.host = host;
        this.port = port;
        this.uniqueId = uniqueId;
    }

    /**
     * The external user of the Tracker simply adds a new Position Builder to
     * the Tracker's internal Queue. Then the internal Tracker logic takes care
     * of sending positions and retries in case of connection problems.
     * 
     * @param protoPositionBuilder
     */
    public void sendPositionProto(Traccar.Position.Builder protoPositionBuilder) {
        messageQueue.add(protoPositionBuilder);
        if (!messageLoopRunning) { // TODO: do this right!
            Thread t = new Thread(new MessageLoop(), "MessageLoop");
            t.start();
        }
    }

    /**
     * Alternative Position format supported by Tracker.
     */
    public void sendPositionEntity(Position positionEntity) {
        sendPositionProto(Transformer.entityToProtoPosition(positionEntity));
    }


    /**
     * All newly added Positions are collected in a Queue. Then the Tracker
     * internally submits the positions to the server - as connectivity allows.
     */
    private Queue<Traccar.Position.Builder> messageQueue = new LinkedList<Traccar.Position.Builder>();
    private Traccar.Device.Builder devBuilder;

    /**
     * All Positions are collected in a MessageQueue and the MessageLoop takes
     * care of sending them whenever connectivity allows it.
     *
     * @author kbeigl@jeets.org
     */
    private class MessageLoop implements Runnable {
        public void run() {
            System.out.println("Starting MessageLoop thread");
            messageLoopRunning = true;  // TODO: do this right!
            try {
//              only transmit if queue has more msgs
                while (!messageQueue.isEmpty()) {
                    boolean transmitted = false;
//                  try transmitting until msgs are acknowledged
                    while (!transmitted) {
//                      keep trying and add newly queued msgs until maxNrOfPositions
                        fillDeviceBuilder();
                        System.out.println("'" + uniqueId + "' sending " + devBuilder.getPositionCount()
                                + " Positions to '" + host + ":" + port + "' at " + new Date().getTime()
                                + " (" + messageQueue.size() + " msgs queued)");
                        if (transmitTraccarDevice(devBuilder)) {
                            transmitted = true;
                            devBuilder = null;
//                          reset and continue with new msgs from queue, fillDev, transmit
                        } else {
                            System.err.println("Transmission failed, trying again in "
                                    + (retryInMillis/1000) + " seconds");
                            Thread.sleep(retryInMillis);
                        }
                    }
                }
            } catch (InterruptedException e) {
                System.err.println("MessageLoop was interrupted!");
                e.printStackTrace();
            }
            messageLoopRunning = false;
            System.out.println("MessageLoop done.");
        }
    }

    private boolean transmitTraccarDevice(Builder devBuilder) {
//      set devicetime just before transmission - every time
        for (int pos = 0; pos < devBuilder.getPositionCount(); pos++) {
            devBuilder.getPositionBuilder(pos).setDevicetime(new Date().getTime());
        }
//      keep devBuilder if transmission goes wrong
        Traccar.Device protoDevice = devBuilder.build();
//      use in/for ACK? add fieldin Device?: java.util.UUID xTraceID = java.util.UUID.randomUUID();
        System.out.println("transmitTraccarDevice: " + protoDevice);
        Traccar.Acknowledge ack = TraccarSender.transmitTraccarObject(protoDevice, host, port);
        if (ack == null) 
            return false;
        else {
            System.out.println("received Acknowledge: " + ack + " at " + new Date());
            return true;
        }
    }

    /**
     * The DeviceBuilder represents the message to be sent. As long as the
     * message can't be sent due to connectivity problems the DeviceBuilder can
     * be filled from the queue to a maximum number of messages. After it was
     * sent a new DeviceBuilder can be created and filled ...
     */
    private void fillDeviceBuilder() {
        if (devBuilder == null) {
            devBuilder = Traccar.Device.newBuilder().setUniqueid(uniqueId);
        }
        while (!messageQueue.isEmpty())
            if (devBuilder.getPositionCount() < maxPosPerMsg)
                devBuilder.addPosition(messageQueue.remove());
            else break;
        System.out.println("DeviceBuilder filled with " + devBuilder.getPositionCount() + " positions.");
    }

    /**
     * A simple Tracker method to submit message Strings. 
     * <p>
     * Any String can be composed in an external environment
     * and will be sent to [hostname:port]. <br>
     * A return message (ACK) is not implemented.
     * 
     * The method is static and does not require a Tracker instance.
     * 
     * @author kbeigl@jeets.org
     */
    public static void transmitProtocolString(String protocolMessage, String host, int port) {
        try (
                Socket serverSocket = new Socket(host, port);
                PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true) )
        {
            out.println(protocolMessage);
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + host + ":" + port);
            System.exit(1);
        }
    }
    
    /*
     * Maximum number of positions transmitted in one message.
     */
    private int maxPosPerMsg = 2;

    public int getMaxPosPerMsg() {
        return maxPosPerMsg;
    }

    public void setMaxPosPerMsg(int maxPosPerMsg) {
        this.maxPosPerMsg = maxPosPerMsg;
    }

    /*
     * If transmission fails, retry sending messages in milliseconds.
     */
    private int retryInMillis = 10000;

    public int getRetryInMillis() {
        return retryInMillis;
    }

    public void setRetryInMillis(int retryInMillis) {
        this.retryInMillis = retryInMillis;
    }

}
