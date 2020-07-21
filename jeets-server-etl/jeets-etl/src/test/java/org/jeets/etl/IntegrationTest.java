/**
 * Copyright 2017 The Java EE Tracking System - JeeTS
 * Copyright 2017 Kristof Beiglböck kbeigl@jeets.org
 *
 * The JeeTS Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.jeets.etl;

import junit.framework.TestCase;
import org.apache.camel.spring.Main;

/*  01.11.18 
	this test fails if PG DB does not exist (see trace below) !!
	TODO: override datasource of productive jpa with pg source IN TEST
	create h2 database - for testing !!
	SOLVE WITH 
	https://docs.spring.io/spring/docs/3.0.0.M4/reference/html/ch13s05.html		update from 3.0 to latest !?
	13.5.1.3 LocalContainerEntityManagerFactoryBean
	The LocalContainerEntityManagerFactoryBean gives full control over EntityManagerFactory configuration 
	and is appropriate for environments where fine-grained customization is required.
	
	[ERROR] testEtlRoutes(org.jeets.etl.IntegrationTest)  Time elapsed: 3.383 s  <<< ERROR!
	org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'jpa' defined in file [F:\virtex\github.jeets\jeets-server-jse\jeets-etl\target\classes\META-INF\spring\camel-context.xml]: Cannot resolve reference to bean 'entityManagerFactory' while setting bean property 'entityManagerFactory'; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'entityManagerFactory' defined in file [F:\virtex\github.jeets\jeets-server-jse\jeets-etl\target\classes\META-INF\spring\camel-context.xml]: Invocation of init method failed; nested exception is org.hibernate.service.spi.ServiceException: Unable to create requested service [org.hibernate.engine.jdbc.env.spi.JdbcEnvironment]
	        at org.jeets.etl.IntegrationTest.testEtlRoutes(IntegrationTest.java:28)
	Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'entityManagerFactory' defined in file [F:\virtex\github.jeets\jeets-server-jse\jeets-etl\target\classes\META-INF\spring\camel-context.xml]: Invocation of init method failed; nested exception is org.hibernate.service.spi.ServiceException: Unable to create requested service [org.hibernate.engine.jdbc.env.spi.JdbcEnvironment]
	        at org.jeets.etl.IntegrationTest.testEtlRoutes(IntegrationTest.java:28)
	Caused by: org.hibernate.service.spi.ServiceException: Unable to create requested service [org.hibernate.engine.jdbc.env.spi.JdbcEnvironment]
	        at org.jeets.etl.IntegrationTest.testEtlRoutes(IntegrationTest.java:28)
	Caused by: org.hibernate.exception.JDBCConnectionException: Error calling Driver#connect
	        at org.jeets.etl.IntegrationTest.testEtlRoutes(IntegrationTest.java:28)
	Caused by: org.postgresql.util.PSQLException: FATAL: Datenbank ?traccar3.14? existiert nicht
	        at org.jeets.etl.IntegrationTest.testEtlRoutes(IntegrationTest.java:28)
 */
public class IntegrationTest extends TestCase {
//  Currently this test is only starting the application to check if it boots OK
    public void testEtlRoutes() throws Exception {
//      boot up Spring application context for 5 seconds to check that it works OK
        Main.main("-duration", "3s");
    }
}
