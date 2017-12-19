package org.jeets.managers;

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
    
    public static final String unregistered = "<unregistered>";

    /**
     * Return a Device Message in persistable state.
     * <p>
     * If the Device already exists in the database it is retrieved with related
     * attributes. Position attributes should be replace with those from the
     * incoming Device Message. Events should be handled and new Events can be
     * added after analysis. For example Geozone events GEOFENCE_ENTRY and
     * GEOFENCE_EXIT.
     * <p>
     * If the Device is not found in the database the Device message is returned
     * with the name &ltunregistered&gt and *can be* persisted.
     */
    public Device authorizeDevice(Device inDevice) {
        Device dbDevice = null;
        try {
            dbDevice = dBlookup(inDevice.getUniqueid());
        } catch (Exception e) {
            LOG.error("Problems retrieving Device {}", inDevice.getUniqueid());
        }
        
        if (dbDevice == null) {
//          dbDevice = new Device();
//          dbDevice.setUniqueid(inDevice.getUniqueid());
            inDevice.setName(unregistered);
            LOG.debug("UNREGISTERED Device {}", dbDevice);
            return inDevice;
        } else {
            LOG.debug("Found a registered Device {}", dbDevice);
        }
        
        return dbDevice;
    }

//  not @Transactional !!
    public Device dBlookup(final String uniqueId) throws Exception {
        return em.createNamedQuery("findDeviceByUniqueId", Device.class)
                .setParameter("uniqueid", uniqueId).getSingleResult();
    }

    @Transactional
    public void dBpersist(Device gtsDevice) {   // INSERT ?
//      observe transactions, same em as other methods ? etc.
        System.out.println("persistManagedDevice " + gtsDevice );
//      use merge with return value? un/managed entity ?
//      Device dev = em.merge(gtsDevice);
        em.persist(gtsDevice);  // not necessarily now

//        em.flush(); // now ?????
    }

//    UNTESTED
    @Transactional
    public Device dBmerge(Device gtsDevice) {   // UPDATE ?
//      observe transactions, same em as other methods ? etc.
        System.out.println("mergeManagedDevice " + gtsDevice );
//      use merge with return value? un/managed entity ?
//      Device dev = em.merge(gtsDevice);
        return em.merge(gtsDevice);  // not necessarily now
    }

}
