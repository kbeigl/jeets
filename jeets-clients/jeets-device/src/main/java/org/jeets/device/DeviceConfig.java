package org.jeets.device;

/**
 * Light version of NettyConfiguration (for the time being) with major parameters for NettyProducer
 * configuration (currently via server uri).
 *
 * <p>Object can be passed to Device via Exchange header for every single send call.
 *
 * <p>On a longer term all parameters should be set in a single NettyConfiguration object, which can
 * be used for all Netty settings. Since URI and EP config don't show the same behavior the
 * NettyConfiguration can be transferred into the URI without actually setting it.
 *
 * <p>see CamelNettyProducerHangTest
 */
public class DeviceConfig {

  private String protocol = "tcp";
  private String host = "localhost";

  private int port;
  private long requestTimeout;

  private boolean sync = true;
  private boolean producerPoolEnabled = true;
  private boolean clientMode;
  private boolean useByteBuf;
  private boolean reuseChannel;
  private boolean allowDefaultCodec = true;

  public String getProtocol() {
    return protocol;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public long getRequestTimeout() {
    return requestTimeout;
  }

  public void setRequestTimeout(long requestTimeout) {
    this.requestTimeout = requestTimeout;
  }

  public boolean isSync() {
    return sync;
  }

  public void setSync(boolean sync) {
    this.sync = sync;
  }

  public boolean isProducerPoolEnabled() {
    return producerPoolEnabled;
  }

  public void setProducerPoolEnabled(boolean producerPoolEnabled) {
    this.producerPoolEnabled = producerPoolEnabled;
  }

  public boolean isClientMode() {
    return clientMode;
  }

  public void setClientMode(boolean clientMode) {
    this.clientMode = clientMode;
  }

  public boolean isUseByteBuf() {
    return useByteBuf;
  }

  public void setUseByteBuf(boolean useByteBuf) {
    this.useByteBuf = useByteBuf;
  }

  public boolean isReuseChannel() {
    return reuseChannel;
  }

  public void setReuseChannel(boolean reuseChannel) {
    this.reuseChannel = reuseChannel;
  }

  public boolean isAllowDefaultCodec() {
    return allowDefaultCodec;
  }

  public void setAllowDefaultCodec(boolean allowDefaultCodec) {
    this.allowDefaultCodec = allowDefaultCodec;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  @Override
  public String toString() {
    return "DeviceConfig [protocol="
        + protocol
        + ", host="
        + host
        + ", port="
        + port
        + ", requestTimeout="
        + requestTimeout
        + ", sync="
        + sync
        + ", producerPoolEnabled="
        + producerPoolEnabled
        + ", clientMode="
        + clientMode
        + ", useByteBuf="
        + useByteBuf
        + ", reuseChannel="
        + reuseChannel
        + ", allowDefaultCodec="
        + allowDefaultCodec
        + "]";
  }

  //  NettyConfiguration extends NettyServerBootstrapConfiguration
  //  NettyServerBootstrapConfiguration.toStringBootstrapConfiguration
}
