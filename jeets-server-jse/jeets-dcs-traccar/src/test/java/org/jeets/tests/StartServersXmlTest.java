package org.jeets.tests;

import java.io.File;

import org.apache.camel.test.spring.CamelSpringTestSupport;
//import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class StartServersXmlTest extends CamelSpringTestSupport {

//	@Test
	public void testCamelRoute() throws Exception {

    	File traccarConfig = new File("setup/traccar.xml");
    	assertTrue("setup file does not exist!", traccarConfig.exists());

    	Thread.sleep(5*1000);

//    	add assertions and mock expectations
	}

	@Override
	protected ClassPathXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/META-INF/spring/StartServers.xml");
//      return new ClassPathXmlApplicationContext("/META-INF/spring/ContextInit.xml");
    }

}
