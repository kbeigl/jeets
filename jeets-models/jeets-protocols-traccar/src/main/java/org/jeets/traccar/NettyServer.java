package org.jeets.traccar;

import org.apache.camel.component.netty.ServerInitializerFactory;

/**
 * POJO helper class to provide all server details needed to register a server with camel-netty.
 * Provides all configuration parameters (except the port# which should be handled as Key of an
 * external Map).
 *
 * <p>Note that the TraccarSetup does not register servers nor their Routes by design. The project
 * user can decide use Camel- or Springregistry or whatever is feasible.
 */
public class NettyServer {
  public int port;
  public ServerInitializerFactory factory; // final

  public String protocolName; // final
  public String transport; // final, change to Traccar logic? boolean datagram

  //	public TraccarRoute route;  // manage externally or widen to CamelNettyServer
}
