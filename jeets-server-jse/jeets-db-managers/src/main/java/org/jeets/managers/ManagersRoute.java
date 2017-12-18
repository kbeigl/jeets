package org.jeets.managers;

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
public class ManagersRoute {

    private static final Logger LOG = LoggerFactory.getLogger(ManagersRoute.class);
    
    @BeanInject("ManagersDao")
    ManagersDao dao;

    @Consume(uri = "direct:managers.in")
    @RecipientList
    public String[] consume(Exchange exchange) {
        Device inDevice = (Device) exchange.getIn().getBody();

//      analogy to traccar.BasePipelineFactory 
//        with handlers only 'upstream'
//      each (GeofenceEvent)Handler:
//        Map<Event, Position> analyzePosition(Position position)
//      public GeofenceManager(DataManager dataManager) {
//        super(dataManager, Geofence.class);
//      if (Context.getConfig().getBoolean("event.enable")) { ..

//      1. this class forms the Camel Route for consecutive Handlers
//          which is used to de/activate handlers (and entity/managers)
//          and compose the recipient list for the 'outer' Route.
//      2. between start (consume) and to (return value) 
//            plain Java is applied on the POJO message
//      3. Each handler is backed by an (entity manager) ..

        LOG.info("lookup Device .." + inDevice);
        Device gtsDevice = null;
        try {   // wrap around complete Route
            gtsDevice = dao.lookupDevice(inDevice.getUniqueid());
        } catch (Exception e) {
            LOG.error("");
        }
//      throw new EntityNotFoundException(entityClass.getSimpleName(), id);
//      check isManaged?
        System.out.println("returned " + gtsDevice);
//      retrieve Database positions only (memory!) !
        System.out.println("positions " + gtsDevice.getPositions().size());
//      move this?
        for (Position position : inDevice.getPositions()) {
            position.setDevice(gtsDevice);
        }
        gtsDevice.setPositions(inDevice.getPositions());
//      gtsDevice.setEvents(null);   // TODO
//      gtsDevice.setLastupdate(inDevice.getLastupdate()); new Date() ?
        System.out.println("new positions " + gtsDevice.getPositions().size());

        dao.persistManagedDevice(gtsDevice);
        
//      finally set Camel output ( pass un/managed entity !? )
//      exchange.getIn().setBody(gtsDevice);
        
        return new String[] { "direct:manager1.out" };
//                           ,"activemq:manager2.out"};
    }
}
