
material
========
http://docs.jboss.org/hibernate/orm/5.2/quickstart/html_single/#tutorial_jpa
Hibernate Getting Started Guide
with examples: hibernate-tutorials.zip

https://docs.jboss.org/hibernate/orm/5.2/userguide/html_single/Hibernate_User_Guide.html
Hibernate ORM 5.2.5.Final User Guide

How to generate java files (first cycle 25.11.16)
==========================
* install hibernate-tutorials.zip with maven and import in Eclipse ...
--
http://tools.jboss.org/documentation/howto/hibernate.html
How To: Develop an Application Using the Hibernate Tools

* New Maven Project > maven-archetype-quickstart 
* convert to JPA project (?)
* project > JPA Tools > generate Entities from Tables ...
	uncheck Console Configuration (?)
	DB Connection: PostgeSQL (preconfigured in Eclipse)
	package: org.jeets.traccar.model.jpa
  => generates 23 Entities with JPA annotations (TODO: configure javax.persistence...)
--
	now with Hibernate Tools ..
skip Create a new Hibernate Mapping file

*	create a Hibernate Configuration file:
	Create a new cfg.xml file:
	Click File > New > Other > Hibernate Configuration File (cfg.xml)
	
	DB Dialect: PostgreSQL > get values from connection
	check create Console Configuration:
	
*	Generate Code and Reverse Engineering

--> you can create the following Entities and EJBs (~DAO ~Home):
	- JPA: Entities with javax.persistence.annotations and relations!
	- EJB: pattern TablenameHome > @Stateful, EntityManager .. persist
	
	- Hibernate: = JPA Entities  without annotations + Tablename.hbm.xml files 
	- Hibernate:   TablenameHome without annotations
				with org.hibernate.SessionFactory instead of EM

	EJBs can not reside in /lib folder as persistence.jar 
	-> create and refactor *Home.java to other package for later use
  
  
  
  
  
  
  
  