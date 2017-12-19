package org.jeets.managers;

import java.util.Set;

import org.apache.camel.BeanInject;
import org.apache.camel.Consume;
import org.apache.camel.Exchange;
import org.apache.camel.RecipientList;
import org.jeets.model.traccar.jpa.Device;
import org.jeets.model.traccar.jpa.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Receives Device Entities and processes them through consecutive (or parallel)
 * Managers. By creating a recipient list as return value you *can* copy the
 * message to other URIs in the system.
 * 
 * @author kbeigl@jeets.org
 */
public class HandlerRoute {

    private static final Logger LOG = LoggerFactory.getLogger(HandlerRoute.class);
    
    @BeanInject("ManagersDao")
    ManagersDao dao;
/*
      analogy to traccar.BasePipelineFactory with handlers only 'upstream'
      public GeofenceManager(DataManager dataManager) {
        super(dataManager, Geofence.class);
      if (Context.getConfig().getBoolean("event.enable")) { ..

      1. this class forms the Camel Route for consecutive Handlers
          which is used to de/activate handlers (and entity/managers)
          and compose the recipient list for the 'outer' Route.
      2. between start (consume) and to (return value) 
            plain Java is applied on the POJO message
      3. Each handler is backed by an (entity manager) ..
 */
//  TODO: configurable attributes
    private boolean acceptUnregisteredDevices = false;

//  TODO: replace direct: with activemq: endpoints !
    @Consume(uri = "direct:managers.in")
    @RecipientList(ignoreInvalidEndpoints=true) // (parallelProcessing = true)
    public String[] consume(Exchange exchange) {
        Device inDevice = (Device) exchange.getIn().getBody();

        Device gtsDevice = dao.authorizeDevice(inDevice); // ========
//      check isManaged?
        LOG.info("returned {} with {} positions ", gtsDevice, gtsDevice.getPositions().size());

//      TODO - UNTESTED
        if (acceptUnregisteredDevices && gtsDevice.getName().equals(ManagersDao.unregistered)) {
//          dao.dBpersist(gtsDevice); ?
//          end route and only send unregistered Devices if they will be consumed!!
//          exchange.getIn().setBody(gtsDevice);  ??
//          return new String[] { "activemq:unregisteredDevices" };
        } else {
            attachNewPositions(inDevice, gtsDevice);
//          attachNewEvents   (inDevice, gtsDevice);
        }
        LOG.info("new positions.size {}", gtsDevice.getPositions().size());

//      analyze positions, speed, course, motion ====================
//      each traccar *Handler: 
//      Map<Event, Position> analyzePosition(Position position)
        
//      gtsDevice.setLastupdate(inDevice.getLastupdate()); new Date() ?
        
        
        
//      dao.dBmerge(gtsDevice); ???
//      dao.dBpersist(gtsDevice);
        
//      finally set Camel output
        exchange.getIn().setBody(gtsDevice);
//      return new String[] {}; // dead end 
        return new String[] {"direct:manager1.out" }; 
//              "uri:invalid", "activemq:manager2.out"};
    }

    /**
     * Attach positions from Device Message to Device Entity from database.
     */
    private void attachNewPositions(Device inDevice, Device gtsDevice) {
        Set<Position> dbPositions = gtsDevice.getPositions();
        for (Position position : inDevice.getPositions()) {
            position.setDevice(gtsDevice);
            dbPositions.add(position);
        }
    }

}
