package org.jeets.model.traccar.hibernate;

import java.util.Date;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.jeets.model.traccar.hibernate.Databasechangeloglock;
import org.jeets.model.traccar.hibernate.Events;

//Test works for a single Entity on full DB!
//import de.jeets.model.traccar.hibernate.*;
//import de.jeets.model.traccar.hibernate.Events;
import junit.framework.TestCase;

public class SessionFactoryTest extends TestCase {
	private SessionFactory sessionFactory;

	@Override
	protected void setUp() throws Exception {
//		The SessionFactory is a thread-safe object that is instantiated once to serve the entire application.
		final StandardServiceRegistry registry = 
				new StandardServiceRegistryBuilder()
				.configure() 	// configures settings 
				.build();		// from hibernate.cfg.xml
		try {
//			sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
			sessionFactory = new Configuration().configure().buildSessionFactory();
//			sessionFactory = new Configuration().configure("path/to/hibernate.cfg.xml").buildSessionFactory();
		} catch (Exception e) {
			System.err.println("cannot create sessionFactory: " +  e);
			// The registry would be destroyed by the SessionFactory, but we had
			// trouble building the SessionFactory so destroy it manually.
			StandardServiceRegistryBuilder.destroy(registry);
		}
	}

	@Override
	protected void tearDown() throws Exception {
		if (sessionFactory != null) {
			sessionFactory.close();
		}
	}
	
//	TODO: add assertions for test cases
//	note: DB is created and destroyed for every Test > simplify?
	
	@SuppressWarnings("unchecked")
	public void testSaveDBlogLock() {	// Databasechangeloglock
		// create a couple of events...
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save( new Databasechangeloglock( 11, false ) );
		session.save( new Databasechangeloglock( 12, true ) );
		session.getTransaction().commit();
		session.close();

		// now lets pull events from the database and list them
		session = sessionFactory.openSession();
		session.beginTransaction();
		List result = session.createQuery("from Databasechangeloglock").list();
		for ( Databasechangeloglock event : (List<Databasechangeloglock>) result ) {
			System.out.println("Databasechangeloglock (" + event.getId() + ") : " + event.isLocked());
		}
		session.getTransaction().commit();
		session.close();
	}

//	public void testRegisterTrackerAndSaveEvents() {
//		1. register Tracker (-ID)
//		2. send events with above Device (-ID)
//	}

	@SuppressWarnings("unchecked")
	public void testSaveEvents() {
		// create a couple of events...
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		// Events(int id, String type, Date servertime, String attributes)
		session.save(new Events(98, "x", new Date(), "{}"));
		session.save(new Events(99, "y", new Date(), "{}"));
		session.getTransaction().commit();
		session.close();

		// now lets pull events from the database and list them
		session = sessionFactory.openSession();
		session.beginTransaction();
		List result = session.createQuery("from Events").list();
		for ( Events event : (List<Events>) result ) {
			System.out.println("Event (" + event.getServertime() + ") : " + event.getId());
		}
//		session.flush();
		session.getTransaction().commit();
		session.close();
	}

}
