package org.jeets.managers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.jeets.model.traccar.jpa.Device;
import org.jeets.model.traccar.jpa.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class ManagersDao {
//  TODO: restrict Exception handling to DAO

    private static final Logger LOG = LoggerFactory.getLogger(ManagersDao.class);
    public static final String unregistered = "<unregistered>";

    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    EntityManager em;

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
            dbDevice = dBLookup(inDevice.getUniqueid());
        } catch (Exception e) {
            LOG.error("Problems retrieving Device {}:\n{}", inDevice.getUniqueid(), e);
        }
        
        if (dbDevice == null) {
            inDevice.setName(unregistered);
            LOG.debug("UNREGISTERED Device {}", inDevice.getUniqueid());
//          safety persist and ordered dbLookup ?
            return inDevice;
        } else {
            LOG.debug("Found a registered Device {}", dbDevice.getUniqueid());
        }
        return dbDevice;
    }

//  not @Transactional !!
    public Device dBLookup(final String uniqueId) throws Exception {
        return em.createNamedQuery("findDeviceByUniqueId", Device.class)
                .setParameter("uniqueid", uniqueId)
                .getSingleResult();
    }

//  not @Transactional !!
    public List<Position> loadPositions(final String uniqueId, Position fromPosition) {
//      move to Entity as @NamedQuery (name =" loadPositions", query = ..
        TypedQuery<Position> query = em                             // typed Position List
                .createQuery("select distinct p from Position p, Device d "  // two related entities
                        + "where p.device.uniqueid=:uniqueid "      // implicit join !
                        + "  and p.fixtime>=:fromdate "
                        + "order by p.fixtime ", Position.class)    // ascending !
//              .setFirstResult(100)                                // offset
                .setMaxResults(10)                                  // pagination
                .setParameter("uniqueid", uniqueId)
                .setParameter("fromdate", fromPosition.getFixtime());
        return query.getResultList();
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
