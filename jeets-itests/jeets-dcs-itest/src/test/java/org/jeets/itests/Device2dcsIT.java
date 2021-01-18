package org.jeets.itests;

import java.io.File;

import org.apache.camel.CamelContext;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.jeets.dcs.FileRoute;
import org.jeets.dcs.Main;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

/*
 * These tests can be executed on various stages assembling a Tracking System
 * via JeeTS. Basically all tests are sending hex messages to the associated
 * protocol port where they can be processed.
 * <p>
 * road map: the dev2dcs ITests should have a common
 * test-jar which can be compiled before ITests and shared amongst them (can be
 * achieved with Maven). This jar can even hold JUnit @Tests which can be
 * scanned. see
 * https://stackoverflow.com/questions/10496846/run-junit-tests-contained-in-
 * dependency-jar-using-maven-surefire "There is a way of running a test in
 * maven from another jar. from maven-surefire-plugin version 2.15 you can tell
 * maven to scan your test jars for tests and run them. You don't need to
 * extract the tests jar. Just add a dependency to your test jar" ..
 * OR: create IT with only Device as external process and go inside server
 * OR: create vm:// Endpoints for Device, Server and ITest
 */
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = Main.class)
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
@MockEndpoints
//@EnableAutoConfiguration	???
public class Device2dcsIT { // extends Camel/TestSupport 

    private static final Logger LOG = LoggerFactory.getLogger(Device2dcsIT.class);

    private String sendFolder, sentFolder;

	@Autowired
	private CamelContext context;

    @Test
    public void testProtocolFiles() throws Exception {
    	
//        PropertiesComponent props = (PropertiesComponent) context.getPropertiesComponent();
//        props.setLocation("classpath:folders.properties");
//        sendFolder = context.resolvePropertyPlaceholders("{device.send.folder}");
        
    	sendFolder = "C:\\kris\\virtex\\github.jeets\\jeets-clients\\jeets-device\\send";
        System.out.println("SENDFOLDER: " + sendFolder);
        
//      sync as parameters with device uri (external? -> via jeets.props)
        sentFolder = sendFolder + "/.sending/.sent/";

//      make sure to remove files from previous tests
//        Assert.assertTrue(deleteDirectory(sendFolder));

        String[] testfiles = new String[] { "teltonika.jdev" };
//              , "ruptela.jdev", "ruptela-teltonika.jdev" };
        for (int i = 0; i < testfiles.length; i++) {
            testProtocolFile(testfiles[i]);
        }

        File file = new File(sentFolder);
        Assert.assertTrue(file.isDirectory());
        Assert.assertEquals(testfiles.length, file.listFiles().length);
//      what about .error and dead letters?
    }

    private void testProtocolFile(String fileName) throws Exception, InterruptedException {
    	
        context.addRoutes( new FileRoute(fileName) );
    	
//      currently 5 seconds seems to be the minimum, see notes in RouteBuilder
        Thread.sleep(20*1000); // change to NotifyBuilder !! to save time for many many test files ..

//      FIXME
//      60 seconds works to wait for delivery problem
//      Failed delivery for . Exhausted after delivery attempt: 1 
//      No consumers available on endpoint: direct://traccar.model.
//      see snippet from tracker2dcs in DcsIT.bak with TypeConverters!

        LOG.info("Check if " + fileName + " was processed ..");
        File target = new File(sentFolder + fileName);
//      hack: wait until file exists
//        assertTrue(fileName + " was not sent!", target.exists());
//        assertTrue(fileName + " was not sent!", target.isFile());

//      actually we're waiting for the second Device route !
//      how to sync NB with 'device done'?
//      NotifyBuilder notify = new NotifyBuilder(context).whenDone(1).create();
//      assertTrue(notify.matchesMockWaitTime());
//      boolean matches = notify.matches(5, TimeUnit.SECONDS);
//      assertTrue(matches);
//      remove Route with filename
        
//      scan log files - for all tests:
//      try logging client and server to one log file > parse and assert in IT !!
//      String content = context.getTypeConverter().convertTo(String.class, target);
//      assertEquals("IMEI", content);
//      assert hex <> expected ACK für req_resp
//      assert hex != Excception && hex = out
//      use logging timestamps (time window) to verify in-out
    }

//    @Override
//    protected CamelContext createCamelContext() throws Exception {
//        CamelContext context = super.createCamelContext();
////      PropertiesComponent props = context.getComponent("properties", PropertiesComponent.class); // Camel 2 
//        PropertiesComponent props = (PropertiesComponent) context.getPropertiesComponent();        // Camel 3  
//        props.setLocation("classpath:folders.properties");
//        sendFolder = context.resolvePropertyPlaceholders("{{device.send.folder}}");
////      sync as parameters with device uri (external? -> via jeets.props)
//        sentFolder = sendFolder + "/.sending/.sent/";
//        return context;
//    }

}
