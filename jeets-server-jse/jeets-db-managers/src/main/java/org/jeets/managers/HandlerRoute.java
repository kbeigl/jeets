package org.jeets.managers;

import java.util.List;
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
    
//  TODO: implement SortedSet for entities dev.positions dev.events dev.geofences
//  docs.jboss.org/hibernate/orm/4.2/manual/en-US/html_single/#collections-sorted
//  JPA specified !?
    
    @BeanInject("ManagersDao")
    private ManagersDao dao;
    
    @BeanInject("GeofenceManager")
    private GeofenceManager geofenceManager;
/*
      analogy to traccar.BasePipelineFactory with handlers only 'upstream'
      public GeofenceManager(DataManager dataManager) {
        super(dataManager, Geofence.class);
      if (Context.getConfig().getBoolean("event.enable")) { ..
 */
//  TODO: configurable attributes
    private boolean acceptUnregisteredDevices = false;

//  TODO: replace direct: with activemq: endpoints !
//  TODO: JMS remoting !
    @Consume(uri = "direct:managers.in")
    @RecipientList(ignoreInvalidEndpoints=true) // (parallelProcessing = true)
    public String[] consume(Exchange exchange) {

        Device inDevice = (Device) exchange.getIn().getBody();
        Device gtsDevice = dao.authorizeDevice(inDevice);
        
//      TODO: create Test for activemq:unregisteredDevices Endpoint 
        if (acceptUnregisteredDevices && gtsDevice.getName().equals(ManagersDao.unregistered)) {
//          TODO - UNTESTED
//          dao.dBpersist(gtsDevice); ?
//          dao.dBmerge  (gtsDevice); ?
        } else {
//          end route, return method and send unregistered Device to MQ
//          exchange.getIn().setBody(gtsDevice);  ??
//          return new String[] { "activemq:unregisteredDevices" };
        }
        
//      merge incoming with managed device ?here?at end of process!
//      replace (for persisting) instead of adding!!
//      gtsDevice.getPositions().addAll(inDevice.getPositions());

        LOG.info("new positions.size {}", gtsDevice.getPositions().size());
        
        geofenceManager.analyzeGeofences(inDevice, gtsDevice);

//      analyze positions, speed, course, motion ====================
//      each traccar *Handler: 
//      Map<Event, Position> analyzePosition(Position position)
//      gtsDevice.setLastupdate(inDevice.getLastupdate()); new Date() ?
        
//      ?device = dao.dBmerge(gtsDevice); ???
//      dao.dBpersist(gtsDevice);
        
//      finally set Camel output
        exchange.getIn().setBody(gtsDevice);
//      return new String[] {}; // dead end 
        return new String[] {"direct:manager1.out" }; 
//              "uri:invalid", "activemq:manager2.out"};
    }

}
