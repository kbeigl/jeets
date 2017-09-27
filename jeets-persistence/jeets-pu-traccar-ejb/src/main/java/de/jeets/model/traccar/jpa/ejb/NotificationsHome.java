package de.jeets.model.traccar.jpa.ejb;
// Generated 26.11.2016 12:59:23 by Hibernate Tools 5.2.0.Beta1

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jeets.model.traccar.jpa.Notifications;

/**
 * Home object for domain model class Notifications.
 * @see de.jeets.model.traccar.jpa.Notifications
 * @author Hibernate Tools
 */
@Stateless
public class NotificationsHome {

	private static final Log log = LogFactory.getLog(NotificationsHome.class);

	@PersistenceContext
	private EntityManager entityManager;

	public void persist(Notifications transientInstance) {
		log.debug("persisting Notifications instance");
		try {
			entityManager.persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void remove(Notifications persistentInstance) {
		log.debug("removing Notifications instance");
		try {
			entityManager.remove(persistentInstance);
			log.debug("remove successful");
		} catch (RuntimeException re) {
			log.error("remove failed", re);
			throw re;
		}
	}

	public Notifications merge(Notifications detachedInstance) {
		log.debug("merging Notifications instance");
		try {
			Notifications result = entityManager.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Notifications findById(int id) {
		log.debug("getting Notifications instance with id: " + id);
		try {
			Notifications instance = entityManager.find(Notifications.class, id);
			log.debug("get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
}
