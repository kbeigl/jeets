package org.jeets.dcs;

import org.apache.camel.builder.RouteBuilder;

/**
 * Create (pseudo dynamic) Camel Route to move the specified fileName to the device's send folder to
 * be sent. Note that this builder is always using the file name as routeId. Using the same id
 * again, will quietly stop and replace the earlier route. Therefore the developer should make sure
 * that routes for different files should be created sequentially after the predecessor has actually
 * moved its file.
 */
public class SendFileRoute extends RouteBuilder {

  private final String fileName, fromFolder, toFolder;

  /*
   * TODO: create @Config @Bean factory with fileName argument and with from/to
   * folders from @TestPropertySource("/folders.properties") and @Autowire and
   * remove this constructor, i.e. the Spring way. Arguments are provided from
   * test for the time being.
   */
  public SendFileRoute(String fileName, String fromFolder, String toFolder) {
    this.fileName = fileName;
    this.fromFolder = fromFolder;
    this.toFolder = toFolder;
  }

  // public SendFileRoute(String fileName) {
  // 	this.fileName = fileName;
  // }

  @Override
  public void configure() throws Exception {
    from("file://" + fromFolder + "?noop=true&fileName=" + fileName)
        //      don't use dynamic ID in production
        .routeId(fileName)
        .log("sending file .. fileName ..")
        .to("file://" + toFolder);
    // TODO: poll target folder to trigger next file
    //
    // stackoverflow.com/questions/33542002/wait-for-all-files-to-be-consumed-before-triggering-next-route
    // TODO end/stop/remove Route after success to create new Routes .. !!
  }
}
