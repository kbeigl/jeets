package org.jeets.model.traccar.jpa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jeets.model.traccar.util.Samples;

//import org.junit.AfterClass;
//import org.junit.BeforeClass;

import junit.framework.TestCase;

/* Note: This is a copy from jpa project.  */
public class PersistenceTest extends TestCase {

    private EntityManagerFactory entityManagerFactory;
    private String uniqueid = "395389", uniqueid2 = "device2";
    private String deviceName = "testTracker";
    private EntityManager entityManager;

    public void testPersistence() {
        entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Device device1 = Samples.createDeviceWithPositionWithTwoEvents();
        device1.setUniqueid(uniqueid);
        device1.setName(deviceName);
//        assertEquals(2, device1.getPosition().getEvents().size());
        entityManager.persist(device1); // merge ?

        Device device2 = Samples.createDeviceEntity();
        device2.setUniqueid(uniqueid2);
        entityManager.persist(device2);
        
//      add: persist position only - with device relation
//      retrieve device with new position!
        
        entityManager.getTransaction().commit();
        entityManager.close();
        assertDatabaseEntries();
    }

    /**
     * Create a new EntityManager and query Database.
     */
    private void assertDatabaseEntries() {
        entityManager = entityManagerFactory.createEntityManager();
        {
            List<Device> result = entityManager.createQuery("from Device", Device.class).getResultList();
            assertEquals(2, result.size());
            
            result = entityManager.createNamedQuery("findDeviceByUniqueId", Device.class)
                    .setParameter("uniqueid", uniqueid).getResultList();
            assertEquals(1, result.size());
            Device device = result.get(0);
            assertEquals(deviceName, device.getName());

//            Position position = device.getPosition();
//            assertEquals(deviceName, position.getDevice().getName());
//            Set<Event> eventSet = position.getEvents();
//            assertEquals(2, eventSet.size());

//          TODO: filter by where position = ..
            List<Event> eventList = entityManager.createQuery("from Event", Event.class).getResultList();            
            assertEquals(2, eventList.size());
            
            result = entityManager.createNamedQuery("findDeviceByUniqueId", Device.class)
                    .setParameter("uniqueid", uniqueid2).getResultList();
            assertEquals(1, result.size());
            device = result.get(0);
//            assertEquals(null, device.getPosition());
            
        }
        entityManager.close();
    }

    @Override
    protected void setUp() throws Exception {
        // Code executed before each test
        // an EntityManagerFactory is set up once for an application
        // IMPORTANT: use original persistence.xml and only modify driver!
        Map<String, String> persistenceMap = new HashMap<String, String>();
        persistenceMap.put("javax.persistence.jdbc.url", "jdbc:h2:mem:db1;DB_CLOSE_DELAY=-1;MVCC=TRUE");
//        persistenceMap.put("javax.persistence.jdbc.url", "jdbc:h2:mem:test;INIT=create schema IF NOT EXISTS generic;");
        persistenceMap.put("javax.persistence.jdbc.user", "sa");
        persistenceMap.put("javax.persistence.jdbc.password", "");
        persistenceMap.put("javax.persistence.jdbc.driver", "org.h2.Driver");
        
        persistenceMap.put("hibernate.hbm2ddl.auto", "create-drop");
//        persistenceMap.put("hibernate.hbm2ddl.auto", "create");
        persistenceMap.put("hibernate.show_sql", "true");
        
        entityManagerFactory = Persistence.createEntityManagerFactory("jeets-pu-traccar-jpa", persistenceMap);
        System.out.println("created EntityManagerFactory jeets-pu-traccar-jpa");
    }

    @Override
    protected void tearDown() throws Exception {
        entityManagerFactory.close();
    }
    
}
