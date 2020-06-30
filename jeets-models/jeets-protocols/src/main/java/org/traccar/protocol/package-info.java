/**
 * The protocols in this package are modeled after the Traccar Protocols and can
 * be compiled into jeets-protocols-traccar project seamlessly to add protocols
 * to the original Traccar release.
 * <p>
 * Note that Traccar Protocols create and return Acknowledge messages from
 * inside the Netty Pipeline (the Netty way), while the Jeets Protocols apply
 * the Camel way to handle, validate messages at the Netty Endpoint and then
 * send back the Acknowledgement via Netty to the client.
 */
package org.traccar.protocol;
