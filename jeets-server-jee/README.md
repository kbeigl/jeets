## WildFly Camel Examples

This directory contains a suite of useful modules to demonstrate various features of the WildFly Camel Subsystem.
Their aim is to provide small, specific and working examples that can be used for reference in your own projects.

### Prerequisites

Please refer to the project [README documentation](https://github.com/wildfly-extras/wildfly-camel/blob/master/README.md) for information on how to build and test the project.
Please take into consideration the minimum Java and Maven requirements. The examples also require a running application server
with the wildfly-camel subsystem deployed.

### Running Examples

Each example aims to be interactive to help you learn 
how to get started with the WildFly Camel Subsystem. 
Each example can be accessed by changing into the example source directory, 
building the project `mvn clean install` and then deploying
to a running application server `mvn install -Pdeploy`.

Examples can be undeployed from a running application server 
by running `mvn clean -Pdeploy`.

-------------------------------
	JeeTS
	=====
	original example-camel-activemq split in two projects:
	
	(move down to project?)
	WildFly must be running 
	The enumerated steps below can all be executed in
	...\jeets-server-jee>
	
	Or individually in the sub projects
	...\jeets-server-jee\jeets-wildfly-activemq-adapter>
	Install activemq-rar
		1. mvn install -Pdeploy-amq-rar  
	RE-Install activemq-rar > INFO Replaced deployment
		2. mvn install -Pdeploy-amq-rar
	UN-Install activemq-rar > INFO Replaced deployment
		3. mvn clean -Pdeploy-amq-rar
		
	...\jeets-server-jee\example-camel-activemq>
	Install example-camel-activemq
		1. mvn install -Pdeploy  
	RE-Install example-camel-activemq > INFO Replaced deployment
		2. mvn install -Pdeploy
	UN-Install example-camel-activemq > INFO Replaced deployment
		3. mvn clean -Pdeploy



	CURRENTLY THE HIGHER LEVEL POMs ALSO EXECUTE clean and install
	TODO: All projects without -Profile should be skipped !!
	      ( activeByDefault tag/flag ? )
	
	
	
	
	
	
	
	
	
	
	
	
	