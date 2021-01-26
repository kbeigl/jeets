package org.jeets.device;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class RouteParseFile extends RouteBuilder {

  /*
   * with DefaultErrorHandler the file will be moved in the .error folder,
   * if host is unreachable. (introduce switch?)
   */
  @Override
  public void configure() throws Exception {

    errorHandler(
        deadLetterChannel("log:dead?level=ERROR")
            .maximumRedeliveries(3)
            .backOffMultiplier(2)
            .retryAttemptedLogLevel(LoggingLevel.WARN)
            .useExponentialBackOff());

    from("direct:parse.file")
        .split()
        .tokenize("\n")
        .streaming()
        .to("direct:setconf.fileone")
        //          .filter instead of .when !?
        .filter()
        .simple("${header.deviceconfig} != null")
        .to("direct:device.send.hex");
  }
}
