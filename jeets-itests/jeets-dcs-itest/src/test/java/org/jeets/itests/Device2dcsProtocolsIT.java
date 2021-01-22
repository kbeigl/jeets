package org.jeets.itests;

import java.io.File;
import java.util.concurrent.TimeUnit;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.test.junit4.TestSupport; // deleteDirectory
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.jeets.dcs.Main;
import org.jeets.dcs.SendFileRoute;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;

/**
 * These tests can be executed on various stages assembling a Tracking System via JeeTS. Basically
 * all tests are sending hex messages to the associated protocol port where they can be processed.
 */
/*
 * road map: the dev2dcs ITests should have a common
 * test-jar which can be compiled before ITests and shared amongst them (can be
 * achieved with Maven). This jar can even hold JUnit @Tests which can be
 * scanned. see
 * https://stackoverflow.com/questions/10496846/run-junit-tests-contained-in-
 * dependency-jar-using-maven-surefire "There is a way of running a test in
 * maven from another jar. from maven-surefire-plugin version 2.15 you can tell
 * maven to scan your test jars for tests and run them. You don't need to
 * extract the tests jar. Just add a dependency to your test jar" ..
 */
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = Main.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@MockEndpoints
@TestPropertySource("/folders.properties")
public class Device2dcsProtocolsIT { // extends Camel/TestSupport

  private static final Logger LOG = LoggerFactory.getLogger(Device2dcsProtocolsIT.class);

  @Value("${data.send.folder}")
  private String dataSendFolder;

  @Value("${device.send.folder}")
  private String deviceSendFolder;

  private String deviceSentFolder;

  @Autowired private CamelContext context;

  @Test
  public void testProtocolFiles() throws Exception {
    deviceSentFolder = deviceSendFolder + "/.sending/.sent/";

    // start from scratch (include in mvn clean <device project> ?
    Assert.assertTrue(TestSupport.deleteDirectory(deviceSentFolder));

    String[] testfiles = new String[] {"teltonika.jdev", "ruptela.jdev", "ruptela-teltonika.jdev"};
    // TODO: wireless-login.jdev, wireless-ruptela.jdev ... one file with all?
    for (int i = 0; i < testfiles.length; i++) {
      testProtocolFile(testfiles[i]);
    }

    File file = new File(deviceSentFolder);
    Assert.assertTrue(file.isDirectory());
    Assert.assertEquals(testfiles.length, file.listFiles().length);
    // what about .error and dead letters?
  }

  @SuppressWarnings("unused")
  private void testProtocolFile(String fileName) throws Exception, InterruptedException {
    LOG.info("sending " + fileName + " to DCS ...");

    // parameter 2 (msgs) works for current test files, improve, generalize ..
    NotifyBuilder notify = new NotifyBuilder(context).whenDone(2).create();
    // see TODO in SendFileRoute
    context.addRoutes(new SendFileRoute(fileName, dataSendFolder, deviceSendFolder));
    // implicit generalization! fine tune for different file length ..
    Assert.assertTrue(notify.matches(20, TimeUnit.SECONDS));
    // wait a bit until file is moved from .sending to .sent
    Thread.sleep(2 * 1000);
    context.removeRoute(fileName);

    File target = new File(deviceSentFolder + fileName);
    // hack: wait until file exists
    Assert.assertTrue(fileName + " was not sent!", target.exists());
    Assert.assertTrue(fileName + " was not sent!", target.isFile());
    LOG.info(fileName + " was sent!");

    // scan log files - for all tests:
    // try logging client and server to one log file > parse and assert in IT !!
    // String content = context.getTypeConverter().convertTo(String.class, target);
    // assertEquals("IMEI", content);
    // assert hex <> expected ACK für req_resp
    // assert hex != Excception && hex = out
    // use logging timestamps (time window) to verify in-out
  }
}
