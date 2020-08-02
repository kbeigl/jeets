package org.jeets.itests;

import java.io.File;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * These tests can be executed on various stages assembling a Tracking System
 * via JeeTS. Basically all tests are sending hex messages to the associated
 * protocol port where they can be processed.
 * <p>
 * For example jeets-protocols-traccar and jeets-dcs-traccar use the same Netty
 * pipelines to decode binary messages. The first project holds the packages
 * originating from Traccar and the latter adds Spring. This should point you to
 * the problems on their first occurrence.
 * <p>
 * Currently this integration test (IT) can be copied into other ITs without
 * changes. In the long run a separate test-jar might be created for maven
 * import.
 */
/*
 * developer note, i.e. road map: the dev2server ITests should have a common
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
/*
 * see java docs, comments and developer notes in DcsSingleMessagesIT
 */
// MUST RUN AFTER DcsSingleMessagesIT !! alphabetical order ?
// currently also waiting for Tracker protobuffer messages!
// .. disconnecting after each message ?
public class Device2dcsIT extends CamelTestSupport { // pure Camel, no Spring

    private static final Logger LOG = LoggerFactory.getLogger(Device2dcsIT.class);

    private String sendFolder, sentFolder;

    @Test
    public void testProtocolFiles() throws Exception {
//      make sure to remove files from previous tests
        Assert.assertTrue(deleteDirectory(sendFolder));

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
        context.addRoutes( new FileRouteBuilder(fileName) );
//      currently 5 seconds seems to be the minimum, see notes in RouteBuilder
        Thread.sleep(5*60*1000); // change to NotifyBuilder !! to save time for many many test files ..
//      FIXME
//      60 seconds works to wait for delivery problem
//      Failed delivery for . Exhausted after delivery attempt: 1 
//      No consumers available on endpoint: direct://traccar.model.
//      see snippet from tracker2dcs in DcsIT.bak with TypeConverters!

        LOG.info("Check if " + fileName + " was processed ..");
        File target = new File(sentFolder + fileName);
//      hack: wait until file exists
        assertTrue(fileName + " was not sent!", target.exists());
        assertTrue(fileName + " was not sent!", target.isFile());

//      consider logging to a single log for device and protocols
//      logFileScanner (use timestamps to sync msg with ack ..)
        
//      actually we're waiting for the second Device route !
//      how to sync NB with 'device done'?
//      NotifyBuilder notify = new NotifyBuilder(context).whenDone(1).create();
//      assertTrue(notify.matchesMockWaitTime());
//      boolean matches = notify.matches(5, TimeUnit.SECONDS);
//      assertTrue(matches);
        
//      scan log files - for all tests:
//      try logging client and server to one log file > parse and assert in IT !!
//      String content = context.getTypeConverter().convertTo(String.class, target);
//      assertEquals("IMEI", content);
//      assert hex <> expected ACK für req_resp
//      assert hex != Excception && hex = out
//      use logging timestamps (time window) to verify in-out
    }

    /**
     * Create (pseudo dynamic) Camel Route to move the specified fileName to the
     * device's send folder to be sent. Note that this builder is always using the
     * file name as routeId. Using the same id again, will quietly stop and replace
     * the earlier route. Therefore the developer should make sure that routes for
     * different files should be created sequentially after the predecessor has
     * actually moved its file.
     * <p>
     * A single dynamic route via .pollEnrich is an alternative: <br>
     * stackoverflow.com/questions/36948005/how-do-dynamic-from-endpoints-and-exchanges-work-in-camel
     */
    private final class FileRouteBuilder extends RouteBuilder {
        private final String fileName;

        private FileRouteBuilder(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public void configure() throws Exception {
//          TODO modify files timestamps to 'now'
//          with PropertyPlaceholder!
            from("file://{{data.send.folder}}?noop=true&fileName=" + fileName)
//          always reuse, i.e. override previous ID
//          .routeId("send-file")
//          don't use dynamic ID in production
            .routeId(fileName)
//          .log("sending file .. fileName ..")
            .to("file://{{device.send.folder}}");
//          TODO: poll target folder to trigger next file
//          stackoverflow.com/questions/33542002/wait-for-all-files-to-be-consumed-before-triggering-next-route
//          camel.apache.org/components/latest/file-component.html
//          BATCH CONSUMER > EXCHANGE PROPERTIES
//          CamelBatchSize, CamelBatchComplete
//          from("file://" + sentFolder + "?noop=true&fileName=" + fileName)
//          .log("sent folder ${body}"); camelFileName ..
//          .to(done?) EP for NotifyBuilder!?

//          TODO end/stop/remove Route after success to create new Routes .. !!
        }
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext context = super.createCamelContext();
//      PropertiesComponent props = context.getComponent("properties", PropertiesComponent.class); // Camel 2 
        PropertiesComponent props = (PropertiesComponent) context.getPropertiesComponent();        // Camel 3  
        props.setLocation("classpath:folders.properties");
        sendFolder = context.resolvePropertyPlaceholders("{{device.send.folder}}");
//      sync as parameters with device uri (external? -> via jeets.props)
        sentFolder = sendFolder + "/.sending/.sent/";
        return context;
    }

}
