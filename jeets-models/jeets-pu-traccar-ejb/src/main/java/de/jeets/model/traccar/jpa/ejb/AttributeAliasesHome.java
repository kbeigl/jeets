package de.jeets.model.traccar.jpa.ejb;
// Generated 26.11.2016 12:59:23 by Hibernate Tools 5.2.0.Beta1

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jeets.model.traccar.jpa.AttributeAliases;

/**
 * Home object for domain model class AttributeAliases.
 * @see de.jeets.model.traccar.jpa.AttributeAliases
 * @author Hibernate Tools
 */
@Stateless
public class AttributeAliasesHome {

	private static final Log log = LogFactory.getLog(AttributeAliasesHome.class);

	@PersistenceContext
	private EntityManager entityManager;

	public void persist(AttributeAliases transientInstance) {
		log.debug("persisting AttributeAliases instance");
		try {
			entityManager.persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void remove(AttributeAliases persistentInstance) {
		log.debug("removing AttributeAliases instance");
		try {
			entityManager.remove(persistentInstance);
			log.debug("remove successful");
		} catch (RuntimeException re) {
			log.error("remove failed", re);
			throw re;
		}
	}

	public AttributeAliases merge(AttributeAliases detachedInstance) {
		log.debug("merging AttributeAliases instance");
		try {
			AttributeAliases result = entityManager.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public AttributeAliases findById(int id) {
		log.debug("getting AttributeAliases instance with id: " + id);
		try {
			AttributeAliases instance = entityManager.find(AttributeAliases.class, id);
			log.debug("get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
}
