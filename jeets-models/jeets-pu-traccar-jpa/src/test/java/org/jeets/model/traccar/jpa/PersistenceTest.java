package org.jeets.model.traccar.jpa;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jeets.model.traccar.util.Samples;
import junit.framework.TestCase;

/* TODO:
 * en.wikipedia.org/wiki/JUnit A JUnit test fixture is a Java object. 
 * With older versions of JUnit, fixtures had to inherit from junit.framework.TestCase, 
 * but the new tests using JUnit 4 should not do this...
 * https://stackoverflow.com/questions/42471723/
 *  jpa-not-recognizing-existing-constraints-from-database
 */
public class PersistenceTest extends TestCase {

    private EntityManagerFactory entityManagerFactory = null;

/*
    @BeforeClass
    public static void setUpClass() throws Exception {
        // Code executed before the first test method
    }
 */

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
        
//        persistenceMap.put("hibernate.hbm2ddl.auto", "create-drop");
        persistenceMap.put("hibernate.hbm2ddl.auto", "create");
        persistenceMap.put("hibernate.show_sql", "true");
	    
		entityManagerFactory = Persistence.createEntityManagerFactory("jeets-pu-traccar-jpa", persistenceMap);
		System.out.println("created EntityManagerFactory jeets-pu-traccar-jpa");
	}

	@Override
	protected void tearDown() throws Exception {
	    // Code executed after each test 
		entityManagerFactory.close();
	}
	
/*
    @AfterClass
    public static void tearDownClass() throws Exception {
        // Code executed after the last test method 
    }
 */

    private String deviceName = "myTracker";
    private EntityManager entityManager;

    public void testPersistence() {
        entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        {
            // reactivate one test with IDs after DB creation - only!
            // withIds();
            Device device = Samples.createDeviceWithPositionWithTwoEvents();
            device.setUniqueid(Samples.unique);
            device.setName(deviceName);
            // persist with dependencies!
            entityManager.persist(device);
            
            device = Samples.createDeviceEntity();
            device.setUniqueid("TestDevice");
//          device.setUniqueid(Samples.unique);
//          assert: PK violation > RollbackException
//          TODO: create and persist position
            entityManager.persist(device);
        }
        entityManager.getTransaction().commit();
        entityManager.close();
        
        assertDatabaseEntries();
    }

    /**
     * This test uses explicit ID values and only works for an empty database,
     * since the IDs are 'guessed' and might already exist in the tables.
     */
    private void withIds() {
//      create cascaded entities
        Device device = new Device(1, deviceName, Samples.unique);

        Position pos1 = new Position(1, device, new Date(), new Date(), new Date(), true, 
                49.12d, 12.56d, 333, 11.2, 121, 0.1);
        pos1.setProtocol("protocol");  // not included in constructor (?)
        Position pos2 = new Position(2, device, new Date(), new Date(), new Date(), true, 
                49.34d, 12.78d, 333, 11.2, 121, 0.1);
        pos2.setProtocol("protocol");

        Event event1 = new Event(1, device, "deviceMoving",  new Date(), 1, 1, "");
        Event event2 = new Event(2, device, "deviceStopped", new Date(), 1, 1, "");

//      Note that EntityManager is not needed to create entities above!
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        {
            // persist in order of dependencies!
            entityManager.persist(device);
            entityManager.persist(pos1);
            entityManager.persist(pos2);
            entityManager.persist(event1);
            entityManager.persist(event2);
        }
        entityManager.getTransaction().commit();
        entityManager.close();

        assertDatabaseEntries();
    }

    /**
     * This method executes database queries via an explicit EntityManager
     * without transactions.
     */
    private void assertDatabaseEntries() {
        EntityManager entityManager;
        entityManager = entityManagerFactory.createEntityManager();
        { // no transaction required
            List<Device> devices = entityManager.createQuery("from Device", Device.class).getResultList();
            assertEquals(2, devices.size());
            Device dev = devices.get(0);    // order is not guaranteed!
            assertEquals(deviceName, dev.getName());
            System.out.println("Device: " + dev.getId() + ", " + dev.getUniqueid() + ", " + dev.getName());
            assertEquals(1, dev.getPositions().size());
            for (Position pos : dev.getPositions())
                System.out.println("Position[" + pos.getId() + "]:" + 
                        pos.getLatitude() + ", " + pos.getLongitude());
            // device events relation does not exist in database!
            assertEquals(0, dev.getEvents().size());
            List<Event> events = entityManager.createQuery("from Event", Event.class).getResultList();
            assertEquals(2, events.size());
        }
        entityManager.close();
    }

}
