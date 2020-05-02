package org.jeets.device;

import java.util.Arrays;

import org.apache.camel.Body;
import org.apache.camel.Consume;
import org.apache.camel.Header;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBufUtil;

/**
 * This Device is basically a wrapper for the camel-netty4 ProducerTemplate with
 * NettyProducer acting as one or more TCP client/s interacting with the server.
 * The idea is to create a Device Endpoint with a Producer to be handled in the
 * Camel Context (not lower levels like Netty or java.net). The Device wraps the
 * communication result in an Exchange for further analysis.
 * <p>
 * This Device is focused on sending and receiving plain byte[] arrays, i.e.
 * plain network communication. The Raw Device can serve as a generic client to
 * send (and receive) byte[] arrays regardless of their proprietary protocol
 * format. For convenience the arrays can also be sent as hex messages, i.e.
 * Strings.
 */
public class Device {

    public Device() { // to be removed
        System.out.println("create Device");
    }

    /*
     * Currently synchronous request-reply and send are implemented. Later their
     * asynchronous counter parts should be added with Futures or Callback to
     * collect transmission results in any order. (collect how/long and when?)
     * camel.apache.org/manual/latest/async.html
     */
    private static final Logger log = LoggerFactory.getLogger(Device.class);

    @Produce
    private ProducerTemplate device;

    /**
     * Convenience method to convert hex message to byte[] message and send it to
     * the server.
     * <p>
     * see {@link #sendByteMessage} for details
     */
    @Consume(uri = "direct:device.send.hex")
    public String sendHexMessage( @Body String hexString, @Header("DeviceConfig") DeviceConfig deviceConfig ) throws Throwable {
        log.info("    send hex: " + hexString );
        byte[] response = sendByteMessage( decodeHexDump(hexString), deviceConfig );
        String hexResponse = hexDump(response);
        if (hexResponse.length() > 0)
            log.info("returned hex: " + hexResponse );
        else
            log.info("hex transmitted successfully");
//      more programmatic to use byte[] as return?
        return hexResponse;
    }

    /**
     * This method represents the Device's Endpoint Consumer to accept a byte[]
     * message with the DeviceConfig for the next send call via NettyProducer.
     * <p>
     * The method branches to InOut request-reply or InOnly request-only
     * communication by sync value. We can make use of the Camel redelivery service
     * to try sending again, if we have connection problems. Camel's error handling
     * concept takes place in channels between nodes on a route path. A channel acts
     * as a controller to monitor and control the routing at runtime. Therefore an
     * explicit DeviceRoute was created to allow Camel to resend the message without
     * bothering the caller.
     */
//  TODO @Consume(uri = "direct:device.send.bytes")
    public byte[] sendByteMessage( @Body byte[] byteMessage, @Header("DeviceConfig") DeviceConfig deviceConfig ) throws Throwable {
        log.debug("send message: " + Arrays.toString(byteMessage) );

//      HARD CODED
//      TODO check deviceConfig != null
        deviceConfig.setAllowDefaultCodec(false);
        deviceConfig.setProducerPoolEnabled(false);
        deviceConfig.setUseByteBuf(true);
        String serverUri = composeServerUri( deviceConfig );
        log.debug("to server: " + serverUri);

        byte[] response = null;
        if (deviceConfig.isSync()) {
            response = device.requestBody(serverUri, byteMessage, byte[].class);
        } else {
            device.sendBody(serverUri, byteMessage);
            response = new byte[]{ /* empty */ };
        }
        log.debug("response: " + Arrays.toString(response));
        return response;
    }

    /**
     * Parse server parameters to set protocol, host, port, sync to specify server
     * uri (or endpoint).
     * <p>
     * Currently this URI is dedicated to the use case and not
     * yet general use.
     */
    private String composeServerUri(DeviceConfig config) {

        return "netty:" + config.getProtocol() + "://" + config.getHost() + ":" + config.getPort()
            + "?sync=" + (config.isSync() ? "true" : "false")
            + "&allowDefaultCodec=" + (config.isAllowDefaultCodec() ? "true" : "false")
//          DEBUG NettyConfiguration + No encoders and decoders will be used
            + "&producerPoolEnabled=" + (config.isProducerPoolEnabled() ? "true" : "false")
            + "&useByteBuf=" + (config.isUseByteBuf() ? "true" : "false");

//          + ", requestTimeout=" + requestTimeout + ", clientMode=" + clientMode + ", 
//          + ", reuseChannel=" + reuseChannel
    }
    
    private byte[] decodeHexDump(String hexString) {
        return ByteBufUtil.decodeHexDump(hexString);
//      this is not part of java +8 anymore
// 2.   javax.xml.bind.DatatypeConverter.parseHexBinary(hexString);
// 3.   org.apache.commons.codec.binary.Hex
//      try {return Hex.decodeHex(string);}
//      catch (DecoderException e) {}
// 4.   core Java, see javadoc to revert to int
//      print("<0x"+Integer.toHexString(c)+">");
    }
    
    private String hexDump(byte[] bytes) {
        return ByteBufUtil.hexDump(bytes);
    }

}
