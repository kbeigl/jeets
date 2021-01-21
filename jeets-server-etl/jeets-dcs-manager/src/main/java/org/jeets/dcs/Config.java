package org.jeets.dcs;

import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.jeets.protocol.JeetsClientProtocol;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Use this class to implement / test individual DCSs. */
@Configuration
public class Config {

  //  jeets-protocols -------------------------------------------------
  //  TODO provide prop file with protocols and ports

  //  jeets-protocols-traccar -----------------------------------------
  //  A dynamic solution should be created for the TypeConverters hard
  //  coded in /resources/META-INF/services/org/apache/camel/TypeConverter

  //  jeets-protocols with Traccar logic ------------------------------
  @Bean(name = "ack") // TODO: "jeets" <> jeets traccar!
  public JeetsClientProtocol getDevicePipeline() {
    return new JeetsClientProtocol(null);
  }

  //  Netty En/Decoder out of the box ! -------------------------------

  @Bean(name = "stringDecoder")
  public StringDecoder createStringDecoder() {
    return new StringDecoder();
  }

  @Bean(name = "stringEncoder")
  public StringEncoder createStringEncoder() {
    return new StringEncoder();
  }

  /**
   * Note that this boolean variable is attached to the URI as String 'true' / 'false'. Maybe apply
   * String for type safety. private boolean camelNettySync = false; camel-netty and/or spring
   * are/is tedious about localhost, which doesn't accept external access (in ubuntu). On the remote
   * system 0.0.0.0 should be used instead of 127.0.0.1. private String host = "0.0.0.0";
   */
}
