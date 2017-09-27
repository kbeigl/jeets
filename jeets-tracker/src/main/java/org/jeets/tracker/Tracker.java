package org.jeets.tracker;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jeets.tracker.netty.TraccarSender;
import org.jeets.protocol.Traccar;

/**
 * The JeeTS Tracker sends Protobuffer Messages, i.e. POJOs defined in the
 * *.proto file. External clients can only transmit Positions while the Tracker
 * internally adds Device with uniqueId and potential Tracker Events!
 * 
 * @author kbeigl@jeets.org
 */

/* TODO: add NioEventLoopGroup management instance for complete runtime
 * tracker.transmitTraccarPosition(position)
 * 
 * Every Tracker instance has a uniqueId and a List<Traccar.Position> as internal database.
 * The Tracker can be configured to send these positions to host and port as needed and as connectivity allows.
 * The Client (like the Player) submits the Traccar.Position in real (playback) time.
 * The Tracker can be configured to send a number of Positions at a time,
 * to send by a time interval (flush database). Accordingly the Tracker can clean up
 * the List after successful ACK or retry if not successful.
 */
public class Tracker implements ProtoPositionListener {

//  add configuration file --------------------------------
    private String uniqueId = "pb.device";
    private String host = "localhost";
    private int port = 5200;
//  public Tracker( ) { properties() }
//  -------------------------------------------------------

    public Tracker( String host, int port, String uniqueId ) {
        this.host = host;
        this.port = port;
        this.uniqueId = uniqueId;
    }

//  add methods to add positions to the tracker, i.e. positionList
//  consider LinkedList and other Collections for this purpose
//  in any case the provided fixtimes must be in chronological order
    private List<Traccar.Position.Builder> positionBuilderList = new ArrayList<>();

    /**
     * Convenience method to send a single Position.
     * Useful for development, may be removed in future!
     * 
     * The position is wrapped in the Device, i.e. this Tracker.
     * Note the a position should provide the FixTime and 
     * the Tracker adds the DeviceTime immediately before sending.
     */
    private void transmitSingleTraccarPosition(Traccar.Position.Builder protoPosition) {
        Traccar.Device.Builder devBuilder = createDeviceBuilder();
//      set Device-,i.e. Tracker time just before transmission
        protoPosition.setDevicetime(new Date().getTime());
        devBuilder.addPosition(protoPosition);
        Traccar.Device protoDevice = devBuilder.build();
        Traccar.Acknowledge ack = TraccarSender.transmitTraccarObject(protoDevice, host, port);
        System.out.println(new Date() + " received Acknowledge: " + ack );
    }

    /**
     * Transmit methods are private and triggered internally by configuration.
     */
    private void transmitTraccarDevice(Traccar.Device protoDevice) {
        Traccar.Acknowledge ack = TraccarSender.transmitTraccarObject(protoDevice, host, port);
        System.out.println("received Acknowledge: " + ack  + " at " + new Date().getTime());
    }
    
    /**
     * Create a Traccar.Device message builder with tracker's uniquId
     * which should be registered on the server.
     */
    private Traccar.Device.Builder createDeviceBuilder() {
        Traccar.Device.Builder deviceBuilder = Traccar.Device.newBuilder();
//      override ?
        deviceBuilder.setUniqueid(uniqueId);
        return deviceBuilder;
    }

    /**
     * A simple Tracker method to submit message Strings. 
     * <p>
     * Any String can be composed in an external environment
     * and will be sent to [hostname:port]. <br>
     * A return message (ACK) is not implemented.
     * 
     * @author kbeigl
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
    
    @Override
    public void transmitPositionProto(Traccar.Position.Builder protoPosition) {
//      System.out.println("Tracker received " + protoPosition.toString());
//      temporary
        System.out.println("Device '" + uniqueId + "' sending ProtoPosition to '" + host + ":" + port +"'");
        transmitSingleTraccarPosition(protoPosition);
//      TODO: add pos to positionList .. house keeping ..
    }

}
