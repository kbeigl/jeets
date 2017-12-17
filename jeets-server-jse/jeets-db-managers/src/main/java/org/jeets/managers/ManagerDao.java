package org.jeets.managers;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.apache.camel.Consume;
import org.apache.camel.Exchange;
import org.apache.camel.RecipientList;
import org.jeets.model.traccar.jpa.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManagerDao {

    private static final Logger LOG = LoggerFactory.getLogger(ManagerDao.class);

//  EXTENDED avoids LazyInitializationException: .. could not initialize proxy - no Session
//  stackoverflow.com/questions/11746499/solve-failed-to-lazily-initialize-a-collection-of-role-exception
    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    EntityManager em;

    @Consume(uri = "direct:managers.in")
    @RecipientList
    public String[] consume(Exchange exchange) {

        LOG.info("lookup Device ..");
        Device gtsDevice = null;
        Device inDevice = (Device) exchange.getIn().getBody();
        try {
            gtsDevice = lookupDevice(exchange, inDevice.getUniqueid());
//          gtsDevice = findDeviceByUniqueId(exchange, inDevice.getUniqueid());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//      throw new EntityNotFoundException(entityClass.getSimpleName(), id);
//      check isManaged?
        System.out.println("returned " + gtsDevice);
//      retrieve Database positions only!
        System.out.println("positions " + gtsDevice.getPositions().size());   // ERROR
/*      move this?
        for (Position position : inDevice.getPositions()) {
            position.setDevice(gtsDevice);
        }
        gtsDevice.setPositions(inDevice.getPositions());
//      gtsDevice.setEvents(null);   // TODO
        gtsDevice.setLastupdate(inDevice.getLastupdate());
 */
        return new String[] {"direct:manager1.out"};    // testing single endpoint
//                          ,"activemq:manager2.out"};
    }
    
    private Device lookupDevice(Exchange exchange, final String uniqueId) throws Exception {
//      TODO: return unique Device! not List.
        List<Device> list = em.createNamedQuery("findDeviceByUniqueId", Device.class)
                .setParameter("uniqueid", uniqueId).getResultList();
        Device dev;
        if (list.isEmpty()) { // throw NoResultException
            dev = new Device(); // or return null ..
            dev.setUniqueid(uniqueId);
//          dev.setName("<unregistered>");
            LOG.info("A new Device WILL BE REGISTERED {}", dev);
        } else {
            dev = list.get(0); // managed
            LOG.info("Found a registered Device {}", dev);
        }
        System.out.println("found device: " + dev);
        return dev;
    }

}
