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

/*
 * see java docs, comments and developer notes in DcsSingleMessagesIT
 */
// MUST RUN AFTER DcsSingleMessagesIT !! alphabetical order ?
// currently also waiting for Tracker protobuffer messages!
// .. disconnecting after each message ?
public class Device2dcsIT extends CamelTestSupport {

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
        Thread.sleep(60*1000); // change to NotifyBuilder
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
    }

    private final class FileRouteBuilder extends RouteBuilder {
        private final String fileName;

        private FileRouteBuilder(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public void configure() throws Exception {
//          TODO modify files timestamps to 'now'
            from("file://{{data.send.folder}}?noop=true&fileName=" + fileName)
            .routeId("send-file")
            .to("file://{{device.send.folder}}");
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
