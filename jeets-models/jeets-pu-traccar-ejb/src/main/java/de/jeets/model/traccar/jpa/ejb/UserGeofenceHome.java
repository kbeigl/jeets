package de.jeets.model.traccar.jpa.ejb;
// Generated 26.11.2016 12:59:23 by Hibernate Tools 5.2.0.Beta1

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jeets.model.traccar.jpa.UserGeofence;
import de.jeets.model.traccar.jpa.UserGeofenceId;

/**
 * Home object for domain model class UserGeofence.
 * @see de.jeets.model.traccar.jpa.UserGeofence
 * @author Hibernate Tools
 */
@Stateless
public class UserGeofenceHome {

	private static final Log log = LogFactory.getLog(UserGeofenceHome.class);

	@PersistenceContext
	private EntityManager entityManager;

	public void persist(UserGeofence transientInstance) {
		log.debug("persisting UserGeofence instance");
		try {
			entityManager.persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void remove(UserGeofence persistentInstance) {
		log.debug("removing UserGeofence instance");
		try {
			entityManager.remove(persistentInstance);
			log.debug("remove successful");
		} catch (RuntimeException re) {
			log.error("remove failed", re);
			throw re;
		}
	}

	public UserGeofence merge(UserGeofence detachedInstance) {
		log.debug("merging UserGeofence instance");
		try {
			UserGeofence result = entityManager.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public UserGeofence findById(UserGeofenceId id) {
		log.debug("getting UserGeofence instance with id: " + id);
		try {
			UserGeofence instance = entityManager.find(UserGeofence.class, id);
			log.debug("get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
}
