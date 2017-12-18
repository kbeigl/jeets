package org.jeets.managers;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.jeets.model.traccar.jpa.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class ManagersDao {

    private static final Logger LOG = LoggerFactory.getLogger(ManagersDao.class);

//  EXTENDED avoids LazyInitializationException: 
//  stackoverflow.com/questions/11746499/
//        solve-failed-to-lazily-initialize-a-collection-of-role-exception
    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    EntityManager em;

    public Device lookupDevice(final String uniqueId) throws Exception {
//      TODO: return unique Device! not List.
        List<Device> list = em.createNamedQuery("findDeviceByUniqueId", Device.class)
                .setParameter("uniqueid", uniqueId).getResultList();
//      EntityTransaction trx = em.getTransaction();
//      java.lang.IllegalStateException: Cannot obtain local EntityTransaction from a transaction-synchronized EntityManager
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
        return dev;
    }

    @Transactional
    public void persistManagedDevice(Device gtsDevice) {
//      observe transactions, same em as other methods ? etc.
        System.out.println("persistManagedDevice " + gtsDevice );
//      use merge with return value? un/managed entity ?
//      Device dev = em.merge(gtsDevice);
        em.persist(gtsDevice);  // not necessarily now
        em.flush(); // now ?
    }

}
