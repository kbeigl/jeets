package de.jeets.model.traccar.jpa.ejb;
// Generated 26.11.2016 12:59:23 by Hibernate Tools 5.2.0.Beta1

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jeets.model.traccar.jpa.GroupGeofence;
import de.jeets.model.traccar.jpa.GroupGeofenceId;

/**
 * Home object for domain model class GroupGeofence.
 * @see de.jeets.model.traccar.jpa.GroupGeofence
 * @author Hibernate Tools
 */
@Stateless
public class GroupGeofenceHome {

	private static final Log log = LogFactory.getLog(GroupGeofenceHome.class);

	@PersistenceContext
	private EntityManager entityManager;

	public void persist(GroupGeofence transientInstance) {
		log.debug("persisting GroupGeofence instance");
		try {
			entityManager.persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void remove(GroupGeofence persistentInstance) {
		log.debug("removing GroupGeofence instance");
		try {
			entityManager.remove(persistentInstance);
			log.debug("remove successful");
		} catch (RuntimeException re) {
			log.error("remove failed", re);
			throw re;
		}
	}

	public GroupGeofence merge(GroupGeofence detachedInstance) {
		log.debug("merging GroupGeofence instance");
		try {
			GroupGeofence result = entityManager.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public GroupGeofence findById(GroupGeofenceId id) {
		log.debug("getting GroupGeofence instance with id: " + id);
		try {
			GroupGeofence instance = entityManager.find(GroupGeofence.class, id);
			log.debug("get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
}
