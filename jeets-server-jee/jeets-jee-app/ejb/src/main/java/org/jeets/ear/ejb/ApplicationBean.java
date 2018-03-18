package org.jeets.ear.ejb;

import java.util.List;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.jeets.model.traccar.jpa.Device;

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
    
    public int messageCount = 0;    // at creation

    public void processMessage(Device devMsg) {
        System.out.println("Application: process message from device '" + devMsg.getUniqueid() + "'");
        
//      retrieve uniqueId > exists in HashMap at runtime?
//        no: create vehicle(uId) = List<Device>
//       yes: lookup vehicle(uId)
//         > add Device message

//      collection for messages -> list status on JSF <- compare eHor JSF !
        
        messageCount++;

    }
    
    /* List all vehicles created and tracked at runtime */
    public List<String> getVehicles() {
        return null;
    }

    /* List all messages of a vehicle tracked at runtime */
    public List<Device> getVehicle( String uniqueId ) {
        return null;
    }

    /* List message nr of a vehicle tracked at runtime */
    public Device getMessage( int nr ) {
        return null;
    }

}
