package org.jeets.model.traccar.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import org.jeets.model.schema.EntityManagerUtils;
import org.jeets.model.traccar.util.Samples;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This test class is relying on an existing (test) database with a valid
 * schema. The same class could be executed a second time (search: junit execute
 * test multiple times, see JUnit @RepeatedTest) against a database dynamically
 * created from hibernate.hbm2ddl.auto=create (in setUpClass
 * createEntityManagerFactory) to detect model differences between script- and
 * ORM generation.
 * <p>
 * Currently all tests run Entities directly against the EntityManager. -DAO
 * and/or -Repository constructs will require higher level testing. Transaction
 * Management is also applied manually to get a better understanding. In higher
 * level components transactions can be added via Spring and other automated
 * solutions.
 * <p>
 * Implicitly all Samples should be tested.
 */
public class SamplesTest {

	private static final Logger log = LoggerFactory.getLogger(SamplesTest.class);
/*  
   TODO: added Geofences 17.12.12 -> add to existing tests
    Set<DeviceGeofence> geofenceIds = device.getDeviceGeofences();
    for (DeviceGeofence deviceGeofence : geofenceIds) {
        Geofence geofence = deviceGeofence.getGeofence();
        System.out.println(geofence.getArea());
    }
    2. ensure and assert chronological List<Position> positions 
    	before persist (DB select already is chrono) without EMgr and DB!
    2a. device.setLastPosition (LAZY?) from last chronological position (and event)
    3. model events and subevents: 
    	KEY_ALARM  and AlarmType ALARM_GENERAL / _SOS / _VIBRATION / _MOVEMENT
    	KEY_MOTION and ???       TYPE_DEVICE_MOVING / _STOPPED / _OVERSPEED 
    	(also see proto file and *org.traccar*.model.Event)
*/
	/**
	 * The Device Entity related to Position and Event Entities form the ORM core
	 * for GPS tracking. All atomic database interactions should be modeled in the
	 * JPA module before adding them on a higher level.
	 * <p>
	 * This test is focused on repeated cascaded persists, no merge, no deletions
	 * which should not necessarily be implemented in a GTS API.
	 */
	@Test
	public void cascadedPersistDevice() {

//		application variables with varying references
		Device jpaDevice = null, dbDevice = null;
		String devName = "defaultDevice", devUniqueId = "defaultUID";
//		lots of activity in a single trx
		entityManager.getTransaction().begin();
		{
			devName = Samples.sampleDeviceName + "1";
			devUniqueId = "unique1";
//	      	no position, no events, ~register device
			jpaDevice = Samples.createDeviceEntity(); // new device
			jpaDevice.setUniqueid(devUniqueId);
			jpaDevice.setName(devName);
//			cascaded persist is set in postgres and in Entities, not in h2
			entityManager.persist(jpaDevice); // without trx.commit
			dbDevice = lookupDeviceByUniqueId(devUniqueId);
			assertTrue("Device1 was NOT persisted!", dbDevice.getName().equals(devName));

			Position position = Samples.createPositionEntity();
//			this persists positions without deviceId !!
			jpaDevice.setPositions(Arrays.asList(position)); // same device
//			.. now it works with deviceId
			position.setDevice(jpaDevice);
//			set positionid for last position
			jpaDevice.setLastPosition(position);
			entityManager.persist(jpaDevice); // again inside trx
			dbDevice = lookupDeviceByUniqueId(devUniqueId);
			assertTrue("Position1 was NOT persisted!", dbDevice.getPositions().size() > 0);

			devName = Samples.sampleDeviceName + "2";
			devUniqueId = "unique2";
			jpaDevice = Samples.createDeviceWithTwoPositions(); // new device
			jpaDevice.setUniqueid(devUniqueId);
			jpaDevice.setName(devName);
			entityManager.persist(jpaDevice);
			dbDevice = lookupDeviceByUniqueId(devUniqueId);
			assertTrue("Device2 was NOT persisted!", dbDevice.getName().equals(devName));

			devName = Samples.sampleDeviceName + "3";
			devUniqueId = "unique3";
			jpaDevice = Samples.createDeviceWithTwoPositionsWithEvent();
			jpaDevice.setUniqueid(devUniqueId);
			jpaDevice.setName(devName);
			toStringTest(jpaDevice); // with 2 pos and 2 ev
			entityManager.persist(jpaDevice);
			
			dbDevice = lookupDeviceByUniqueId(devUniqueId);
			assertTrue("Device3 was NOT persisted!", dbDevice.getName().equals(devName));
			toStringTest(dbDevice); // with 2 pos and 2 ev

			devName = Samples.sampleDeviceName + "4";
			devUniqueId = "unique4";
			jpaDevice = Samples.createDeviceWithPositionWithTwoEvents();
			jpaDevice.setUniqueid(devUniqueId);
			jpaDevice.setName(devName);
			entityManager.persist(jpaDevice);
			dbDevice = lookupDeviceByUniqueId(devUniqueId);
			assertTrue("Device4 was NOT persisted!", dbDevice.getName().equals(devName));
		}
		entityManager.getTransaction().commit(); // finally ;)
//		unique1	12:04:34.464	sample performance by fixtimes
//		unique2	12:04:34.71	
//		unique3	12:04:34.74	
//		unique4	12:04:34.798	

		List<Device> dbDevices = entityManager.createQuery("from Device", Device.class).getResultList();
		assertEquals("Four Devices should be stored in the database.", 4, dbDevices.size());

		validateDatabase(dbDevices);
		validateObjectRelations(dbDevices);

	}

	/**
	 * Navigate from Device to Events and Positions.
	 * <p>
	 * Once you get any reference to any Object in the ORM you can reach every
	 * branch and leaf. Test ensures that all existing uni/bidirectional relations
	 * are wired correctly.
	 * <p>
	 * Note that the relation Events-Position does not exist in database, but is
	 * modeled in the ORM with event.getPosition()!
	 */
	private void validateObjectRelations(List<Device> dbDevices) {

		Device dbDevice;
		for (int dev = 0; dev < dbDevices.size(); dev++) { // no order guaranteed
			dbDevice = dbDevices.get(dev);
			final List<Position> positions = dbDevice.getPositions();
//			already asserted: two events for dev3 and dev4
			final List<Event> events = new ArrayList<Event>(dbDevice.getEvents());

			String uniqueId = dbDevice.getUniqueid();
			int uniqueCount = Integer.parseInt(uniqueId.substring(uniqueId.length() - 1));
			log.info("navigate device(unique{}) ...", uniqueCount);

//			test applies for setup above, generally last position is optional
//			relation device.positionid - position is only modeled in ORM, not in database
			assertTrue("Last Position is NOT contained in Device-Positions!",
					positions.contains(dbDevice.getLastPosition()));

			switch (uniqueCount) {
			case 3: // DeviceWithTwoPositionsWithEvent 2/2
				for (int ev = 0; ev < events.size(); ev++) {
					assertTrue("Event-Position is NOT contained in Device-Positions!",
							positions.contains(events.get(ev).getPosition()));
				}
				break;

			case 4: // DeviceWithPositionWithTwoEvents 1/2
				assertSame("The Events do NOT point to the same Position!", 
						events.get(0).getPosition().getFixtime(),
						events.get(1).getPosition().getFixtime());
				break;

			default:
//				log.info("device({}) not validated in this test !?!", dbDevice.getUniqueid());
				break;
			}
		}

	}

	private void validateDatabase(List<Device> dbDevices) {

		Device dbDevice;
		for (int dev = 0; dev < dbDevices.size(); dev++) { // no order guaranteed
			dbDevice = dbDevices.get(dev);
//		    can't validate dev.positions order since fixtimes can be identical in this test
//			@OrderBy("fixtime DESC, devicetime DESC, servertime DESC")
			String uniqueId = dbDevice.getUniqueid();
			int uniqueCount = Integer.parseInt(uniqueId.substring(uniqueId.length() - 1));
			switch (uniqueCount) {
			case 1:
//				log.info("device(unique1): " + dbDevice);	// raises StackOverflowError !!
				log.info("validate device(unique1) ... ");
				assertEquals("Should have exactly one position", 1, dbDevice.getPositions().size());
				break;

			case 2:
				log.info("validate device(unique2) ... ");
				assertEquals("Should have exactly two positions", 2, dbDevice.getPositions().size());
				break;

			case 3:
				log.info("validate device(unique3) ... ");
				assertEquals("Should have exactly two positions", 2, dbDevice.getPositions().size());
				assertEquals("Should have exactly two events", 2, dbDevice.getEvents().size());
				break;

			case 4:
				log.info("validate device(unique4) ... ");
				assertEquals("Should have exactly one position", 1, dbDevice.getPositions().size());
				assertEquals("Should have exactly two events", 2, dbDevice.getEvents().size());
				break;

			default:
				log.error("device({}) not expected in this test !?!", dbDevice.getUniqueid());
				break;
			}
		}
	}

	/**
	 * All Entities should have a .toString() method and the relations should be
	 * displayed conservatively in order not to raise a StackOverflowError due to
	 * recursive .toString() Device > Positions > Position > Device invocations.
	 */
	private void toStringTest(Device device) {
//		silent test in background
		device.toString();
//		log.info(device.toString());
		device.getPositions().get(0).toString();
//		log.info(device.getPositions().get(0).toString());
		final List<Event> events = new ArrayList<Event>(device.getEvents());
		events.get(0).toString();
//		log.info(events.get(0).toString());
	}

	private Device lookupDeviceByUniqueId(String uniqueId) {
		Device dev = entityManager.createNamedQuery("findDeviceByUniqueId", Device.class)
				.setParameter("uniqueid", uniqueId).getSingleResult();
		return dev;
	}

	private static EntityManagerUtils emProvider;

	@BeforeClass
	public static void setUpClass() {
		log.debug("creating EntityManager");
		emProvider = new EntityManagerUtils();
//		setup em once for all tests
		entityManager = emProvider.createEntityManager(EntityManagerUtils.PERSISTENCE_UNIT_NAME, null);
//		this works (sql-maven-plugin can/should be turned off)
//		entityManager = emProvider.hibernateAutoCreate(EntityManagerUtils.PERSISTENCE_UNIT_NAME);		
//		more ways to get em ...
	}

	private static EntityManager entityManager;

	@Before
	public void setUp() throws Exception {
		log.info("clear database ...");
//		don't truncate after test -> database inspection
		truncateTables();
	}

	private void truncateTables() {
		entityManager.getTransaction().begin();
//		this works for h2 not pg; find syntax for postgres (and create h2/pg split)
		entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
		entityManager.createNativeQuery("truncate table tc_positions").executeUpdate();
		entityManager.createNativeQuery("truncate table tc_events").executeUpdate();
		entityManager.createNativeQuery("truncate table tc_devices").executeUpdate();
		entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
		entityManager.getTransaction().commit();
		log.info("truncated {} tables", 3);
	}

	@After
//  use one static EntityManager instance for all tests (implicit testing)
	public void flushEntityManager() throws Exception {
		log.debug("flushEntityManager");
		if (entityManager != null) {
			entityManager.getTransaction().begin();
			entityManager.flush();
			entityManager.getTransaction().commit();
//			checkDatabase();
//			don't close em here
		}
	}

/*  implement as needed
	private void checkDatabase() {
		Query query = entityManager.createQuery("select a from tc_devices as d");
		for (Object o : query.getResultList()) {
			log.info("EM_AUTO: {}", o);
		}
	}
	TODO: Validate database content on a higher level than a single ORM.
	public void validateDatabaseContent() {
		// some snippets to start with
		List<Device> result;
		result = entityManager.createQuery("from Device", Device.class).getResultList();
		result = entityManager.createNamedQuery("findDeviceByUniqueId", Device.class)
				.setParameter("uniqueid", Samples.uniqueId).getResultList();
		List<Event> eventList = entityManager.createQuery("from Event", Event.class).getResultList();
    }
*/

	@AfterClass
	public static void tearDownClass() {
		emProvider.cleanupEntityManager(entityManager);
	}

}
