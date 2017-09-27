package de.jeets.model.traccar.jpa.ejb;
// Generated 26.11.2016 12:59:23 by Hibernate Tools 5.2.0.Beta1

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jeets.model.traccar.jpa.Devices;

/**
 * Home object for domain model class Devices.
 * @see de.jeets.model.traccar.jpa.Devices
 * @author Hibernate Tools
 */
@Stateless
public class DevicesHome {

	private static final Log log = LogFactory.getLog(DevicesHome.class);

	@PersistenceContext
	private EntityManager entityManager;

	public void persist(Devices transientInstance) {
		log.debug("persisting Devices instance");
		try {
			entityManager.persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void remove(Devices persistentInstance) {
		log.debug("removing Devices instance");
		try {
			entityManager.remove(persistentInstance);
			log.debug("remove successful");
		} catch (RuntimeException re) {
			log.error("remove failed", re);
			throw re;
		}
	}

	public Devices merge(Devices detachedInstance) {
		log.debug("merging Devices instance");
		try {
			Devices result = entityManager.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Devices findById(int id) {
		log.debug("getting Devices instance with id: " + id);
		try {
			Devices instance = entityManager.find(Devices.class, id);
			log.debug("get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
}
