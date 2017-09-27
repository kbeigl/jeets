The JeeTS and ROAF Source Repository	May 2016
====================================
This main directory (jeets) is hosting the code  
	-------------------------------------------
 for	the JEE Tracking System (i.e. 'G'TS)  JeeTS
	-------------------------------------------
 and	the Real Object Application Framework ROAF
	-------------------------------------------
While jeets is targeting at a Tracking System like OpenGTS, traccar and many more.
It is purely technical, general purpose and customizable.

The roaf on the other hand should be kept free of technologies
as a general purpose 'real world library'.
The latter will eventually end up in a seperate repo.

Description
-----------
Development is done against JBoss WildFly:
Version		Date			Description
10.0.0.Final	2016-01-29	Java EE7 Full & Web Distribution

System Requirements
-------------------
The components and applications were tested with: 
	Apache Maven 3.3.3 (7994120775791599e205a5524ec3e0dfe41d4a06; 2015-04-22T13:57:37+02:00)
	Java version: 1.8.0_60, vendor: Oracle Corporation
   	Eclipse Mars with JBoss Tools 4.3.1 (with Arquillian Testing Tool)

To run the modules with the provided build scripts, 
you need the the WildFly distribution ZIP: 

Install WildFly via Eclipse (and JBoss Tools)
---------------------------------------------
Define a New Server > WildFly10.0 localhost > local filesystem and shell
Name: WildFly 10.0 Runtime
Download and Install Runtime
Project  URL: wildfly.org/downloads
Download URL: download.jboss.org/wildfly/10.0.0.Final/wildfly-10.0.0.Final.zip
HomeDir: C:\prox\wildfly-10.0.0.Final
server base dir: standalone
config file: standalone.xml

23.05.2016
	a) Management User (mgmt-users.properties)
	   Username : wfadmin
	   Password : l@texx16

C:\prox\wildfly-10.0.0.Final\bin>add-user.bat

What type of user do you wish to add?
 a) Management User (mgmt-users.properties)
 b) Application User (application-users.properties)
(a):

Enter the details of the new user to add.
Using realm 'ManagementRealm' as discovered from the existing property files.
Username : wfadmin
Password : l@texx16
What groups do you want this user to belong to? 
(Please enter a comma separated list, or leave blank for none)[  ]:
Added user 'wfadmin' to file 'C:\prox\wildfly-10.0.0.Final\standalone\configuration\mgmt-users.properties'
Added user 'wfadmin' to file 'C:\prox\wildfly-10.0.0.Final\domain\configuration\mgmt-users.properties'
Added user 'wfadmin' with groups  to file 'C:\prox\wildfly-10.0.0.Final\standalone\configuration\mgmt-groups.properties'
Added user 'wfadmin' with groups  to file 'C:\prox\wildfly-10.0.0.Final\domain\configuration\mgmt-groups.properties'
Is this new user going to be used for one AS process to connect to another AS process?
yes/no? n


Main pom.xml file in the root directory jeets/
----------------------------------------------
..is based on the quickstart directory from git.
To run a full compilation (clean test package install) on all modules:
In Eclipse > project: jeets-sources > run as.. Maven clean install 
or from command line: mvn clean install

currently not applied
---------------------
1. settings.xml in root directory
2. github.com/jboss/jboss-parent-pom 
The  <parent>   
        <groupId>org.jboss</groupId>
        <artifactId>jboss-parent</artifactId>
        <version>19</version>
        <relativePath/>
	</parent>
raises some **ignorable** git problems:
	[INFO] --- buildnumber-maven-plugin:1.3:create (get-scm-revision) @ jeets-sources ---
	[INFO] Executing: cmd.exe /X /C "git rev-parse --verify HEAD"
	[INFO] Working directory: F:\virtex\jeets
	[INFO] Storing buildNumber: UNKNOWN at timestamp: 1463513669392
	[WARNING] Cannot get the branch information from the git repository:
	Detecting the current branch failed: 
	Der Befehl "git" ist entweder falsch geschrieben oder konnte nicht gefunden werden.
	[INFO] Executing: cmd.exe /X /C "git rev-parse --verify HEAD"
	[INFO] Working directory: F:\virtex\jeets
	[INFO] Storing buildScmBranch: UNKNOWN_BRANCH
    
This version uses the correct dependencies and ensures 
you test and compile against your runtime environment.

Verify the complete Build with One Command
------------------------------------------
You can verify the build using one command. 
However, quickstarts that have complex dependencies must be skipped. 
For example, the `resteasy-jaxrs-client` quickstart is a RESTEasy client 
	that depends on the deployment of the _helloworld-rs_ quickstart. 
As noted above, the root `pom.xml` file defines a `complex-dependencies` profile 
to exclude these modules from the root build process. 

To build the quickstarts:

1. DO NOT START the WildFly server.
2. Open a command prompt and navigate to the root directory jeets/
3. Use this command to build the quickstarts that do not have complex dependencies:
          mvn clean install -Pdefault,!complex-dependencies

May 2016:	mvn clean install
		is sufficient, since all modules are commented in the pom.xml

Note: 
If you see a `java.lang.OutOfMemoryError: PermGen space` error when you run this command, 
increase the memory by typing the following command for your operating system:
        For Linux:   export MAVEN_OPTS="-Xmx512m -XX:MaxPermSize=128m"
        For Windows:    SET MAVEN_OPTS="-Xmx512m -XX:MaxPermSize=128m"
Note! MaxPermSize is not supported in Java 8 any more.

currently set: MAVEN_OPTS=-Xms2048m -Xmx2048m

prepare managed and remote TESTING
----------------------------------
By setting the wildfly path as system variable
the repo does not need to be modified on a new machine!

If wildfly is already installed and managed by Eclipse
check Preferences > Server > Runtime Environment for path

In Windows set JBOSS_HOME=C:\prox\wildfly-10.0.0.Final
(restart DOS box to get the new system variable)
Note: JBOSS_HOME is backward compatibel to EAP_HOME and WILDFLY_HOME

Alternatively you can add the dedicated configuration 
in the individual projects arquillian.xml

Run modules individually
------------------------
see examples 
...\virtex\jeets\ol4jsf\README.md
...\virtex\jeets\geofox-api-frontend\README.md

The root folder of each individual project contains a README file 
with specific details on how to build and run the example. 
In most cases you do the following:

development cycle	(command line)
-----------------
cd ...\dev\wildfly.10\ejb-in-war	(example from quickstarts)
mvn clean
mvn clean install
(stop server)
mvn clean test -Parq-wildfly-managed
(start server (from IDE))
mvn clean test -Parq-wildfly-remote
mvn clean install wildfly:deploy
http://localhost:8080/wildfly-ejb-in-war/greeter.jsf
mvn clean install wildfly:undeploy

development cycle	(eclipse)
-----------------
(start server (from IDE))
right click project > Maven > select maven profile: arq-wildfly-remote
right click Greeter(EJB)Test > Run As JUnit Test ... green bar :)

to set break points the server has to be started in debug mode!
right click Greeter(EJB)Test > Debug As JUnit Test > stop at break point
... green bar :)



TODO	(Juli'16)
- create/modify ...\virtex\jeets\geofox-api-frontend\README.md file
	with above maven command lines ...
- difference? > Run As JUnit Test <> Arquillian JUnit Test

Note about arq-wildfly-embedded
           ====================
After some analysis I gave up on arq-wildfly-embedded
(as I used glassfish-embedded for debugging).
Also it is not included in the wildfly bom
(which includes an arquillian bom)
and would cause too much mavenization.
And it seems reasonable to work with managed and remote.
Drawback is that the server has to be started in dev mode
in order to set break points for debugging.

Challenges with Maven
---------------------
create batch/sh file to start server, run different maven profiles?
better: analyze quickstart parent pom for jboss release process.
		(and create similar parent)
Also analyze camel pom structure for 'version pom file'.

Currently too much redundant code.
Move managed and remote profiles to settings.xml ?



### Undeploy the Deployed Quickstarts with One Command
------------------------------------------------------

To undeploy the quickstarts from the root of the quickstart folder, you must pass the argument `-fae` (fail at end) on the command line. This allows the command to continue past quickstarts that fail due to complex dependencies and quickstarts that only have Arquillian tests and do not deploy archives to the server.

You can undeploy quickstarts using the following procedure:

1. Start the WildFly server.
2. Open a command prompt and navigate to the root directory of the quickstarts.
3. Use this command to undeploy any deployed quickstarts:

            mvn wildfly:undeploy -fae

To undeploy any quickstarts that fail due to complex dependencies, follow the undeploy procedure described in the quickstart's README file.


Use JBoss Developer Studio or Eclipse to Run the Quickstarts
------------------------------------------------------------

You can also deploy the quickstarts from Eclipse using JBoss tools. For more information on how to set up Maven and the JBoss tools, see the [Red Hat JBoss Enterprise Application Platform Documentation](https://access.redhat.com/documentation/en-US/JBoss_Enterprise_Application_Platform/) _Getting Started Guide_ and _Development Guide_ or [Get Started with JBoss Developer Studio](http://www.jboss.org/products/devstudio/get-started/ "Get Started with JBoss Developer Studio").


Optional Components
-------------------
The following components are needed for only a small subset of the quickstarts. Do not install or configure them unless the quickstart requires it.

* [Create Users Required by the Quickstarts](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/CREATE_USERS.md#create-users-required-by-the-quickstarts): Add a Management or Application user for the quickstarts that run in a secured mode.

* [Configure the PostgreSQL Database for Use with the Quickstarts](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/CONFIGURE_POSTGRESQL_EAP7.md#configure-the-postgresql-database-for-use-with-the-quickstarts): The PostgreSQL database is used for the distributed transaction quickstarts.

* [Configure Byteman for Use with the Quickstarts](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/CONFIGURE_BYTEMAN.md#configure-byteman-for-use-with-the-quickstarts): This tool is used to demonstrate crash recovery for distributed transaction quickstarts.



