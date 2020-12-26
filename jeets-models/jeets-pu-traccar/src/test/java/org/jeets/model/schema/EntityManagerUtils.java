package org.jeets.model.schema;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.engine.jdbc.internal.DDLFormatterImpl;

/**
 * This class provides different methods to create a schema in a database.
 * Different methods can be manually executed to run the standard tests against.
 * The standard (especially DBUnit-) tests rely on an existing, maybe populated
 * database. The database is not cleaned or dropped after testing and can be
 * inspected with various sql tools like pgAdmin, h2console etc.
 * <p>
 * WARNING! Do not apply these methods to production database as it can be
 * modified, deleted or even dropped!
 * <p>
 * Note that these methods are expensive as each one has to create a new
 * EntityManagerFactory with individual sets of properties. Therefore they
 * should not be (sequentially) invoked in the regular mvn (integration) test
 * cycle. A different approach would be to create individual persistence units
 * in persistence.xml:
 * <br>
 * www.silverbaytech.com/2015/09/21/jpa-2-1-schema-generation/
 * 
 * @author kbeigl@jeets.org
 */
public class EntityManagerUtils {
	private static final Logger log = LoggerFactory.getLogger(EntityManagerUtils.class);
	
	/**
	 * The persistence unit supplies a complete ORM defined by related entity
	 * classes. The JeeTS persistence units should also provide the database
	 * connection defined by driver, url, user and password.
	 */
	public static final String PERSISTENCE_UNIT_NAME = "jeets-pu-traccar-test";
	/**
	 * The EntityManagerFactory provides the database connection to each new
	 * EntityManager and must be static. As the EntityManager can not change its
	 * existing connection the EntityManagerFactory needs to created for each method
	 * - which is too expensive for a regular test cycle.
	 */
	private static EntityManagerFactory emFactory;

	/**
	 * 'main method' to trigger private methods to manually create database and / or
	 * schema scripts. After a database setup all tests can be run against it. 
	 */
//	@Test
	public void createSchema() {
//		EntityManager em = null;
//		TODO: switch (key) { case ...
//		1. drops existing and creates new schema
//		em = hibernateAutoCreate(PERSISTENCE_UNIT_NAME);
//		2. creates drop- and create scripts for mvn text cycle
		createSchemaScripts(PERSISTENCE_UNIT_NAME);

//		reactivate after adding switch statement above
//		if (em != null) {
//			logEntityManagerAndFactory(em);
//			cleanupEntityManager(em);
//		}
	}

	/**
	 * Hibernate feature to drop and create a database from the persistence units
	 * entity classes.
	 * 
	 * @param persistenceUnit
	 */
	public EntityManager hibernateAutoCreate(String persistenceUnit) {
		Properties emProps = new Properties();
//		(only) use this *hibernate feature* for extra cycle of test classes 
//		validate that database remains for inspection after em.close
	    emProps.setProperty("hibernate.hbm2ddl.auto", "create");
	    return createEntityManager(persistenceUnit, emProps);
	}

	/*	TODO: alternative to proprietary hibernateAutoCreate
	public EntityManager jpaAutoCreate(String persistenceUnit) {
		javax.persistence.schema-generation.database.action
		values: "none", "create", "drop-and-create", "drop" 
		compare to script.action in createSchmemaScripts
	}

	TODO: alternative to proprietary sql-maven-plugin 
	public EntityManager jpaAutoCreate(String persistenceUnit) {
		javax.persistence.schema-generation.database.action
		values: "none", "create", "drop-and-create", "drop"
		with
		javax.persistence.sql-load-script-source
		value="path/to/file.sql" 
	}
	
	and different combinations 
	www.thoughts-on-java.org
		/standardized-schema-generation-data-loading-jpa-2-1
		/hibernate-tips-create-database-setup-script-based-entity-mappings/
	including data insertion
	antoniogoncalves.org/2014/12/11/generating-database-schemas-with-jpa-2-1/
	with javax.persistence.sql-load-script-source value="path/to/insert.sql"
 */

	/**
	 * Generate the sql create and drop scripts needed for the regular test cycle.
	 * <p>
	 * This test is not part of the 'mvn test' runs and should be manually applied,
	 * ONLY if the ORM is changed, i.e. after version increments. The method will
	 * replace the drop and create scripts applied by maven.
	 * <p>
	 * Make sure the existing scripts were not manually modified or add
	 * modifications to new scripts! Also be aware that the new scripts will be
	 * appended to the existing ones and need to cleaned accordingly.
	 */
	private void createSchemaScripts(String persistenceUnit) {
		Map<String, Object> emProps = new HashMap<>();
		StringWriter create = new StringWriter();
		StringWriter drop = new StringWriter();
		emProps.put("javax.persistence.schema-generation.scripts.action", "drop-and-create");
		emProps.put("javax.persistence.schema-generation.scripts.create-target", create);
		emProps.put("javax.persistence.schema-generation.scripts.drop-target", drop);
		
        Persistence.generateSchema(persistenceUnit, emProps);
        
        log.info("writing jpa_create script ...");
        prettyPrint(create.toString(), "src/test/resources/ddl/jpa_create.ddl");
        log.info("writing jpa_drop script ...");
        prettyPrint(drop.toString(), "src/test/resources/ddl/jpa_drop.ddl");
//      this should appear on stdout and *not* in the last file
        System.out.println("scripts were executed");
	}

	/**
	 * Apply hibernate proprietary DDL formatter to file:
	 * <br>
	 * see: www.silverbaytech.com/2016/01/30/jpa-2-1-schema-generation-formatted-output/
	 */
	private static void prettyPrint(String unformatted, String file)
	{
		PrintStream stdout = System.out;	// save for reset
		PrintStream fileStream;
		try {
			fileStream = new PrintStream(file);
			System.setOut(fileStream);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		StringReader lowLevel = new StringReader(unformatted);
		BufferedReader highLevel = new BufferedReader(lowLevel);
		DDLFormatterImpl formatter = new DDLFormatterImpl();
		// TODO: enrich with JeeTS details
		System.out.println("-- file created at " + new Date());	
		highLevel.lines().forEach(x -> {
			String formatted = formatter.format(x + ";");
			System.out.println(formatted);
		});
		
		System.setOut(stdout);				// reset
	}

	public EntityManager createEntityManager(String persistenceUnit, Properties emProps) {
		emFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, emProps);
		return emFactory.createEntityManager();
	}
	
	public void cleanupEntityManager(EntityManager em) {
		if (em != null) {
//			log.debug("tearDown() started, em={}", em);
			em.getTransaction().begin();
			em.flush();
			em.getTransaction().commit();
			em.close();
//			log.debug("tearDown() complete, em={}", em);
			em = null;
		}
        if (emFactory != null) {
        	log.info("closing entity manager factory");
            emFactory.close();
            emFactory = null;
        }
	}

	private void logEntityManagerAndFactory(EntityManager em) {
		log.info(	"EntityManagerFactory: " + emFactory.toString() + "\nproperties: ");
		Map<String, Object> props = emFactory.getProperties();
		props.forEach((key, value) -> log.info(key + ":" + value));
		for (Map.Entry<String, Object> entry : props.entrySet()) {
			if (entry.getKey().startsWith("javax.persistence.") || entry.getKey().startsWith("hibernate."))
				log.info(entry.getKey() + ":" + entry.getValue().toString());
		}
		log.info("EntityManager: " + em.toString() + "\nproperties: ");
		props = em.getProperties();
		props.forEach((key, value) -> log.info(key + ":" + value));
	}

}
