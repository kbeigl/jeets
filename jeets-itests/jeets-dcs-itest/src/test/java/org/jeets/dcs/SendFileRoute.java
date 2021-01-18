package org.jeets.dcs;

import org.apache.camel.builder.RouteBuilder;

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
public class SendFileRoute extends RouteBuilder {
    private final String fileName;

//  temporary variables to work around props config problem, TO BE REMOVED
//    String dataSendFolder  ="C:\\kris\\virtex\\github.jeets\\jeets-clients\\jeets-device\\send";
//    String deviceSendFolder="C:\\kris\\virtex\\github.jeets\\jeets-data\\device.send";

    public SendFileRoute(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void configure() throws Exception {
        from("file://C:\\kris\\virtex\\github.jeets\\jeets-data\\device.send?noop=true&fileName=" + fileName)
//      from("file://{data.send.folder}?noop=true&fileName=" + fileName)
//      from("file://" + dataSendFolder + "?noop=true&fileName=" + fileName)
//      don't use dynamic ID in production
        .routeId(fileName)
//      .log("sending file .. fileName ..")
//      .to("file://{device.send.folder}");
//      .to("file://" + deviceSendFolder);
        .to("file://C:\\kris\\virtex\\github.jeets\\jeets-clients\\jeets-device\\send");
//      TODO: poll target folder to trigger next file
//      stackoverflow.com/questions/33542002/wait-for-all-files-to-be-consumed-before-triggering-next-route
//      TODO end/stop/remove Route after success to create new Routes .. !!
    }
}

