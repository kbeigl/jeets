package de.jeets.model.traccar.jpa.ejb;
// Generated 26.11.2016 12:59:23 by Hibernate Tools 5.2.0.Beta1

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jeets.model.traccar.jpa.Groups;

/**
 * Home object for domain model class Groups.
 * @see de.jeets.model.traccar.jpa.Groups
 * @author Hibernate Tools
 */
@Stateless
public class GroupsHome {

	private static final Log log = LogFactory.getLog(GroupsHome.class);

	@PersistenceContext
	private EntityManager entityManager;

	public void persist(Groups transientInstance) {
		log.debug("persisting Groups instance");
		try {
			entityManager.persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void remove(Groups persistentInstance) {
		log.debug("removing Groups instance");
		try {
			entityManager.remove(persistentInstance);
			log.debug("remove successful");
		} catch (RuntimeException re) {
			log.error("remove failed", re);
			throw re;
		}
	}

	public Groups merge(Groups detachedInstance) {
		log.debug("merging Groups instance");
		try {
			Groups result = entityManager.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Groups findById(int id) {
		log.debug("getting Groups instance with id: " + id);
		try {
			Groups instance = entityManager.find(Groups.class, id);
			log.debug("get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
}
