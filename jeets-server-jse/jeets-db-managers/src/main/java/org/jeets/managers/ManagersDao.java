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
            dbDevice = dBLookup(inDevice.getUniqueid());
        } catch (Exception e) {
            LOG.error("Problems retrieving Device {}", inDevice.getUniqueid());
        }
        
        if (dbDevice == null) {
//          dbDevice = new Device();
//          dbDevice.setUniqueid(inDevice.getUniqueid());
            inDevice.setName(unregistered);
            LOG.debug("UNREGISTERED Device {}", inDevice.getUniqueid());
            return inDevice;
        } else {
            LOG.debug("Found a registered Device {}", dbDevice.getUniqueid());
        }
        
        return dbDevice;
    }

//  not @Transactional !!
    public Device dBLookup(final String uniqueId) throws Exception {
//      MOVE RESULT TO NamedQuery in Device
//      -------------------------------------------------------------------------------------------
//      "SELECT d FROM Device d WHERE d.uniqueid = :uniqueid"
        Device dev1 = em
                .createNamedQuery("findDeviceByUniqueId", Device.class)
                .setParameter("uniqueid", uniqueId)
                .getSingleResult();
//      System.out.println("dev1 has " + dev1.getPositions().size() + " positions");
/*
//      -------------------------------------------------------------------------------------------
        TypedQuery<Position> query = em                             // via Position List
                .createQuery("select p from Position p, Device d "
                        + "where p.device.uniqueid=:uniqueid "      // implicit join !
                        + "order by p.servertime ", Position.class)
                .setFirstResult(100)    // pagination
                .setMaxResults(10)
                .setParameter("uniqueid", uniqueId);
        List<Position> positions = query.getResultList();
        int p=1;
//      2 positions listed in 2x5 elements ?
        for (Position position : positions) {
            System.out.println(p++ + ": " + position.getServertime());
        }
        Device dev2 = positions.get(0).getDevice();
        System.out.println("dev2 has " + dev2.getPositions().size() + " positions");
//      -------------------------------------------------------------------------------------------
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-d H:m:s.S");
        Date fromDate = null;
        try { fromDate = formatter.parse("2017-05-20 15:45:01.623");}
        catch (ParseException e){e.printStackTrace();}
        TypedQuery<Device> queryDev = em
                .createQuery("select d "
                        + "from Device d "
                        + "left join fetch d.positions pos "
//                      + "join d.positions pos "
//                      + ", in(d.positions) pos "
//                      + "where pos.servertime > '2017-05-20 15:45:01.623' "
                        + "where pos.servertime>:fromdate "
                        + "and d.uniqueid=:uniqueid "
                        + "order by pos.servertime ", Device.class)
                .setParameter("fromdate", fromDate)
                .setParameter("uniqueid", uniqueId);
//                      + "where pos.protocol=:protocol", Device.class)
//                .setParameter("protocol", "osmand");
//        Device dev3 = queryDev.getSingleResult();
        List<Device> list = queryDev.getResultList();               // via Device List
        System.out.println("list has " + list.size() + " devices");
        Device dev3 = (Device) list.get(0);
        System.out.println("dev3 has " + dev3.getPositions().size() + " positions");
 */
//      -------------------------------------------------------------------------------------------
        return dev1;
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
