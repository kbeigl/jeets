package org.jeets.model.traccar.jpa;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jeets.model.traccar.util.Samples;
import junit.framework.TestCase;

/**
 * This is a copy from jpa project and may be removed in future.
 */
public class PersistenceTesting extends TestCase {

    private EntityManagerFactory entityManagerFactory;

	@Override
	protected void setUp() throws Exception {
		entityManagerFactory = Persistence.createEntityManagerFactory("jeets-pu-traccar-jpa-test");
	}

	@Override
	protected void tearDown() throws Exception {
		entityManagerFactory.close();
	}

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
