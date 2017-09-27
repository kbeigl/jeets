package de.jeets.model.traccar.jpa.ejb;
// Generated 26.11.2016 12:59:23 by Hibernate Tools 5.2.0.Beta1

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jeets.model.traccar.jpa.UserDevice;
import de.jeets.model.traccar.jpa.UserDeviceId;

/**
 * Home object for domain model class UserDevice.
 * @see de.jeets.model.traccar.jpa.UserDevice
 * @author Hibernate Tools
 */
@Stateless
public class UserDeviceHome {

	private static final Log log = LogFactory.getLog(UserDeviceHome.class);

	@PersistenceContext
	private EntityManager entityManager;

	public void persist(UserDevice transientInstance) {
		log.debug("persisting UserDevice instance");
		try {
			entityManager.persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void remove(UserDevice persistentInstance) {
		log.debug("removing UserDevice instance");
		try {
			entityManager.remove(persistentInstance);
			log.debug("remove successful");
		} catch (RuntimeException re) {
			log.error("remove failed", re);
			throw re;
		}
	}

	public UserDevice merge(UserDevice detachedInstance) {
		log.debug("merging UserDevice instance");
		try {
			UserDevice result = entityManager.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public UserDevice findById(UserDeviceId id) {
		log.debug("getting UserDevice instance with id: " + id);
		try {
			UserDevice instance = entityManager.find(UserDevice.class, id);
			log.debug("get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
}
