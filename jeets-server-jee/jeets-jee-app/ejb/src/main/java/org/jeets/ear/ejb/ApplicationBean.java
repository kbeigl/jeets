package org.jeets.ear.ejb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.jeets.model.traccar.jpa.Device;
import org.jeets.model.traccar.jpa.Position;

import javafx.geometry.Pos;

/**
 * A simple Demo Application skeleton to receive Device messages from Active MQ
 * and process them in an application context. The application is instantiated
 * as singleton at deployment time and collects statistics for the complete
 * runtime. Device messages can be managed via Persistence Unit and
 * EntityManager ...
 * 
 * @author kbeigl@jeets.org
 */
@Singleton
@Startup
public class ApplicationBean /* implements MyApplication */ {
    
    private int messageCount = 0;    // at creation and during runtime
    private Device lastMessage;

    private Map<String, Device> vehicles = new HashMap<>();

    /**
     * Process incoming Device messages from MQ Broker for application scenario
     */
    public void processMessage(Device devMsg) {
        System.out.println("Application: process msg#" + ++messageCount + " from device '" + devMsg.getUniqueid() + "'");
        lastMessage = devMsg;
        
//      TODO: remove vehicle / update vehicles when connection is lost (how?)
//        or define timeout (ie lastMessage) for each vehicle and remove from list. 
        if ( !vehicles.containsKey( devMsg.getUniqueid())) {
            // create new entry for device and use original Device 
            // as container for subsequent messages
            System.out.println("add " + devMsg.getUniqueid() + " to collection ...");
            vehicles.put(devMsg.getUniqueid(), devMsg);
        } else {
            System.out.println("add " + devMsg.getPositions().size() + " Positions "
                    + "and " + devMsg.getEvents().size() + " Events "
                    + "to collection " + devMsg.getUniqueid()  +  "...");
            Device dev = vehicles.get( devMsg.getUniqueid() );
            // add Positions and Events to existing vehicle
            List<Position> allPositions = dev.getPositions();
            allPositions.addAll(devMsg.getPositions());
            dev.setPositions(allPositions);
            dev.setLastupdate(devMsg.getLastupdate());
        }
        
//      output for JSF table
        System.out.println("observing " + vehicles.size() + " vehicles ... ");
        for ( String vehicle : vehicles.keySet()) {
            Device vehicleDev = vehicles.get(vehicle);
            System.out.println("vehicle: '" + vehicle 
                    + "' / " + vehicleDev.getPositions().size() + " positions"
                    + "' / " + vehicleDev.getLastupdate() );
        }
    }
    

    /** 
     * List all vehicles created and tracked at runtime.
     * Returned as list for easier JSF handling.
     */
    public List<Device> getVehicles() {
        return new ArrayList<Device>(vehicles.values());
    }

    public Device getVehicle( String uniqueId ) {
        return vehicles.get(uniqueId);
    }

    public int getMessageCount() {
        return messageCount;
    }

    public Device getLastMessage() {
        return lastMessage;
    }

}
