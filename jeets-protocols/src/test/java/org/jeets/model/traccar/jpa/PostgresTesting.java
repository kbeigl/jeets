package org.jeets.model.traccar.jpa;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jeets.model.traccar.util.Samples;

import junit.framework.TestCase;

/**
 * Test direct connection to postgres database. This test is not executed by
 * build tool, since database might not be provided in build environment. It
 * should be used interactively at dev time to run tests against the existing
 * database.
 * <p>
 * persistence.xml can be modified to create a complete postgres database
 * but should not be checked in! In a test environment the db name should be 
 * modified and has to be created in pg in order to run the test.
 * 
 * @author kbeigl@jeets.org
 */
public class PostgresTesting extends TestCase {

    private EntityManagerFactory entityManagerFactory;
    
	@Override
	protected void setUp() throws Exception {
		entityManagerFactory = Persistence.createEntityManagerFactory("jeets-pu-traccar-jpa");
	}

	@Override
	protected void tearDown() throws Exception {
		entityManagerFactory.close();
	}

    private EntityManager entityManager;

    public void testPersistence() {
        entityManager = entityManagerFactory.createEntityManager();
        {
            List<Device> result;
            result = entityManager.createQuery("from Device", Device.class)
                    .getResultList();
            assertTrue(result.size() > 0);
            
            result = entityManager.createNamedQuery("findDeviceByUniqueId", Device.class)
                    .setParameter("uniqueid", Samples.unique).getResultList();
            assertEquals(1, result.size());
            Device device = result.get(0);
            assertEquals(Samples.unique, device.getUniqueid());

//          TODO: filter by where position = ..
            List<Event> eventList = entityManager.createQuery("from Event", Event.class).getResultList();            
            assertTrue(eventList.size() > 0);
        }
        entityManager.close();
    }

}
