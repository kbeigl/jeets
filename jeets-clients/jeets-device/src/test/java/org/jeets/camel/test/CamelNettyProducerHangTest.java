package org.jeets.camel.test;

import java.util.Arrays;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.netty.NettyComponent;
import org.apache.camel.component.netty.NettyConfiguration;
import org.apache.camel.component.netty.NettyEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class CamelNettyProducerHangTest extends CamelTestSupport {

    private static final int   GTS_PORT = 5046;  // traccar GPS Tracking System: Ruptela port
    private static final int CAMEL_PORT = 4093;  // Camel Route below
    private static final String PROTOCOL = "tcp", HOST = "localhost";
//  Ruptela message: 033500000C076B5C208F01011E5268CEF20000196E3A3A0AEF3E934F3E2D780000000007000000005268CEFD0000196E3A3A0AEF3E934F3E2D780000000007000000005268CF080000196E3A3A0AEF3E934F3E2D780000000007000000005268CF130000196E3A3A0AEF3E934F3E2D780000000007000000005268CF1E0000196E3A3A0AEF3E934F3E2D780000000007000000005268CF290000196E3A3A0AEF3E934F3E2D780000000007000000005268CF340000196E3A3A0AEF3E934F3E2D780000000007000000005268CF3F0000196E3A3A0AEF3E934F3E2D780000000007000000005268CF4A0000196E3A3A0AEF3E934F3E2D780000000007000000005268CF550000196E3A3A0AEF3E934F3E2D780000000007000000005268CF600000196E3A3A0AEF3E934F3E2D780000000007000000005268CF6B0000196E3A3A0AEF3E934F3E2D780000000007000000005268CF730000196E36630AEF42CE4F6D0BF40400022208000000005268CF7E0000196E36B60AEF42BE4F6D0BF40000000007000000005268CF890000196E36B60AEF42BE4F6D0BF40000000007000000005268CF940000196E36B60AEF42BE4F6D0BF40000000007000000005268CF9F0000196E36B60AEF42BE4F6D0BF40000000007000000005268CFAA0000196E36B60AEF42BE4F6D0BF40000000007000000005268CFB50000196E36B60AEF42BE4F6D0BF40000000007000000005268CFC00000196E36B60AEF42BE4F6D0BF40000000007000000005268CFCB0000196E36B60AEF42BE4F6D0BF40000000007000000005268CFD60000196E36B60AEF42BE4F6D0BF40000000007000000005268CFD70000196E3C710AEF5EFF4F690BF40400011708000000005268CFE20000196E3B980AEF601A4F690BF40000000007000000005268CFED0000196E3B980AEF601A4F690BF40000000007000000005268CFF80000196E3B980AEF601A4F690BF40000000007000000005268D0030000196E3B980AEF601A4F690BF40000000007000000005268D00E0000196E3B980AEF601A4F690BF40000000007000000005268D0190000196E3B980AEF601A4F690BF40000000007000000005268D0240000196E3B980AEF601A4F690BF400000000070000000046E2
    private static final byte[] byteMessage = new byte[] {3, 53, 0, 0, 12, 7, 107, 92, 32, -113, 1, 1, 30, 82, 104, -50, -14, 0, 0, 25, 110, 58, 58, 10, -17, 62, -109, 79, 62, 45, 120, 0, 0, 0, 0, 7, 0, 0, 0, 0, 82, 104, -50, -3, 0, 0, 25, 110, 58, 58, 10, -17, 62, -109, 79, 62, 45, 120, 0, 0, 0, 0, 7, 0, 0, 0, 0, 82, 104, -49, 8, 0, 0, 25, 110, 58, 58, 10, -17, 62, -109, 79, 62, 45, 120, 0, 0, 0, 0, 7, 0, 0, 0, 0, 82, 104, -49, 19, 0, 0, 25, 110, 58, 58, 10, -17, 62, -109, 79, 62, 45, 120, 0, 0, 0, 0, 7, 0, 0, 0, 0, 82, 104, -49, 30, 0, 0, 25, 110, 58, 58, 10, -17, 62, -109, 79, 62, 45, 120, 0, 0, 0, 0, 7, 0, 0, 0, 0, 82, 104, -49, 41, 0, 0, 25, 110, 58, 58, 10, -17, 62, -109, 79, 62, 45, 120, 0, 0, 0, 0, 7, 0, 0, 0, 0, 82, 104, -49, 52, 0, 0, 25, 110, 58, 58, 10, -17, 62, -109, 79, 62, 45, 120, 0, 0, 0, 0, 7, 0, 0, 0, 0, 82, 104, -49, 63, 0, 0, 25, 110, 58, 58, 10, -17, 62, -109, 79, 62, 45, 120, 0, 0, 0, 0, 7, 0, 0, 0, 0, 82, 104, -49, 74, 0, 0, 25, 110, 58, 58, 10, -17, 62, -109, 79, 62, 45, 120, 0, 0, 0, 0, 7, 0, 0, 0, 0, 82, 104, -49, 85, 0, 0, 25, 110, 58, 58, 10, -17, 62, -109, 79, 62, 45, 120, 0, 0, 0, 0, 7, 0, 0, 0, 0, 82, 104, -49, 96, 0, 0, 25, 110, 58, 58, 10, -17, 62, -109, 79, 62, 45, 120, 0, 0, 0, 0, 7, 0, 0, 0, 0, 82, 104, -49, 107, 0, 0, 25, 110, 58, 58, 10, -17, 62, -109, 79, 62, 45, 120, 0, 0, 0, 0, 7, 0, 0, 0, 0, 82, 104, -49, 115, 0, 0, 25, 110, 54, 99, 10, -17, 66, -50, 79, 109, 11, -12, 4, 0, 2, 34, 8, 0, 0, 0, 0, 82, 104, -49, 126, 0, 0, 25, 110, 54, -74, 10, -17, 66, -66, 79, 109, 11, -12, 0, 0, 0, 0, 7, 0, 0, 0, 0, 82, 104, -49, -119, 0, 0, 25, 110, 54, -74, 10, -17, 66, -66, 79, 109, 11, -12, 0, 0, 0, 0, 7, 0, 0, 0, 0, 82, 104, -49, -108, 0, 0, 25, 110, 54, -74, 10, -17, 66, -66, 79, 109, 11, -12, 0, 0, 0, 0, 7, 0, 0, 0, 0, 82, 104, -49, -97, 0, 0, 25, 110, 54, -74, 10, -17, 66, -66, 79, 109, 11, -12, 0, 0, 0, 0, 7, 0, 0, 0, 0, 82, 104, -49, -86, 0, 0, 25, 110, 54, -74, 10, -17, 66, -66, 79, 109, 11, -12, 0, 0, 0, 0, 7, 0, 0, 0, 0, 82, 104, -49, -75, 0, 0, 25, 110, 54, -74, 10, -17, 66, -66, 79, 109, 11, -12, 0, 0, 0, 0, 7, 0, 0, 0, 0, 82, 104, -49, -64, 0, 0, 25, 110, 54, -74, 10, -17, 66, -66, 79, 109, 11, -12, 0, 0, 0, 0, 7, 0, 0, 0, 0, 82, 104, -49, -53, 0, 0, 25, 110, 54, -74, 10, -17, 66, -66, 79, 109, 11, -12, 0, 0, 0, 0, 7, 0, 0, 0, 0, 82, 104, -49, -42, 0, 0, 25, 110, 54, -74, 10, -17, 66, -66, 79, 109, 11, -12, 0, 0, 0, 0, 7, 0, 0, 0, 0, 82, 104, -49, -41, 0, 0, 25, 110, 60, 113, 10, -17, 94, -1, 79, 105, 11, -12, 4, 0, 1, 23, 8, 0, 0, 0, 0, 82, 104, -49, -30, 0, 0, 25, 110, 59, -104, 10, -17, 96, 26, 79, 105, 11, -12, 0, 0, 0, 0, 7, 0, 0, 0, 0, 82, 104, -49, -19, 0, 0, 25, 110, 59, -104, 10, -17, 96, 26, 79, 105, 11, -12, 0, 0, 0, 0, 7, 0, 0, 0, 0, 82, 104, -49, -8, 0, 0, 25, 110, 59, -104, 10, -17, 96, 26, 79, 105, 11, -12, 0, 0, 0, 0, 7, 0, 0, 0, 0, 82, 104, -48, 3, 0, 0, 25, 110, 59, -104, 10, -17, 96, 26, 79, 105, 11, -12, 0, 0, 0, 0, 7, 0, 0, 0, 0, 82, 104, -48, 14, 0, 0, 25, 110, 59, -104, 10, -17, 96, 26, 79, 105, 11, -12, 0, 0, 0, 0, 7, 0, 0, 0, 0, 82, 104, -48, 25, 0, 0, 25, 110, 59, -104, 10, -17, 96, 26, 79, 105, 11, -12, 0, 0, 0, 0, 7, 0, 0, 0, 0, 82, 104, -48, 36, 0, 0, 25, 110, 59, -104, 10, -17, 96, 26, 79, 105, 11, -12, 0, 0, 0, 0, 7, 0, 0, 0, 0, 70, -30};
//  expected Ruptela ACK: 0002640113bc  

//    @Test
//  SUCCESSFUL REQ - RESPONSE
    public void requestUriCamel() throws Exception {
        String serverUri = "netty4:" + PROTOCOL + "://" + HOST + ":" + CAMEL_PORT + "?sync=true";
        byte[] response = template.requestBody( serverUri, byteMessage, byte[].class);
        log.info("ServerUri response  " + Arrays.toString(response));
        assertEquals( 6, response.length);
    }

//    @Test
    // only sync=true => producer hangs after 'writing body' > Timeout
    // -> unsupported message type: [B (expected: ByteBuf, FileRegion))
    // add &useByteBuf=true => io.netty.handler.codec.TooLongFrameException > Channel is inactive
    public void requestEndpointCamel() throws Exception {
        NettyConfiguration nettyConfig = new NettyConfiguration();
        nettyConfig.setProtocol(PROTOCOL);
        nettyConfig.setHost(HOST);
        nettyConfig.setPort(CAMEL_PORT);
        nettyConfig.setSync(true);
//        nettyConfig.setAllowDefaultCodec(false);
//        nettyConfig.setUseByteBuf(true);
        nettyConfig.setRequestTimeout(2000);    // only used to terminate test
        NettyComponent netty4 = context.getComponent("netty4", NettyComponent.class);
//        netty4.setConfiguration(configuration);
//        netty4.start();
        NettyEndpoint serverEndpoint = new NettyEndpoint(null, netty4, nettyConfig);
//        serverEndpoint.setConfiguration(nettyConfig);
//        serverEndpoint.getEndpointUri()
//        serverEndpoint.start();

        byte[] response = template.requestBody( serverEndpoint, byteMessage, byte[].class);
        log.info("ServerEndpoint response  " + Arrays.toString(response));
        assertEquals( 6, response.length);
    }

//    @Test
    //  SUCCESSFUL REQ - RESPONSE with allowDefaultCodec=false
    public void requestUriGtsUseByteBuf() throws Exception {
        String serverUri = "netty4:" + PROTOCOL + "://" + HOST + ":" + GTS_PORT 
                + "?sync=true&useByteBuf=true&allowDefaultCodec=false";
        byte[] response = template.requestBody( serverUri, byteMessage, byte[].class);
        log.info("ServerUri response  " + Arrays.toString(response));
        assertEquals( 6, response.length);
    }

//    @Test
//  SUCCESSFUL REQ - RESPONSE
    public void requestEndpointGtsUseByteBuf() throws Exception {
        NettyConfiguration nettyConfig = new NettyConfiguration();
        nettyConfig.setProtocol(PROTOCOL);
        nettyConfig.setHost(HOST);
        nettyConfig.setPort(GTS_PORT);
        nettyConfig.setSync(true);
        nettyConfig.setUseByteBuf(true);    // false hangs 'writing body ..'
        NettyComponent netty4 = context.getComponent("netty4", NettyComponent.class);
        NettyEndpoint serverEndpoint = new NettyEndpoint(null, netty4, nettyConfig);
//      NettyProducer producer = (NettyProducer) serverEndpoint.createProducer(); // without CamelTestSupport
        byte[] response = template.requestBody( serverEndpoint, byteMessage, byte[].class);
        log.info("ServerEndpoint response  " + Arrays.toString(response));
        assertEquals( 6, response.length);
    }

//    @Test
    // only sync=true => Hex with heading bytes > java.lang.IndexOutOfBoundsException > disconnect > no response from server
    public void requestUriGts() throws Exception {
        String serverUri = "netty4:" + PROTOCOL + "://" + HOST + ":" + GTS_PORT + "?sync=true";
        byte[] response = template.requestBody( serverUri, byteMessage, byte[].class);
        log.info("ServerUri response  " + Arrays.toString(response));
        assertEquals( 6, response.length);
    }

//    @Test
//  without UseByteBuf(true) => GTS works and returns ACK > producer hangs after 'writing body'
    public void requestEndpointGts() throws Exception {
        NettyConfiguration nettyConfig = new NettyConfiguration();
        nettyConfig.setProtocol(PROTOCOL);
        nettyConfig.setHost(HOST);
        nettyConfig.setPort(GTS_PORT);
        nettyConfig.setSync(true);
        nettyConfig.setRequestTimeout(2000);    // only used to terminate test
        NettyComponent netty4 = context.getComponent("netty4", NettyComponent.class);
        NettyEndpoint serverEndpoint = new NettyEndpoint(null, netty4, nettyConfig);

        byte[] response = template.requestBody( serverEndpoint, byteMessage, byte[].class);
        log.info("ServerEndpoint response  " + Arrays.toString(response));
        assertEquals( 6, response.length);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("netty4://tcp://localhost:4093") // ?sync=true&useByteBuf=true
                .log("server received : ${body.length} bytes")
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        byte[] messageIn = (byte[]) exchange.getIn().getBody();
                        log.info("server processor received " + Arrays.toString(messageIn));
//                      Ruptela ACK: 0002640113BC
                        byte[] messageOut = new byte[] {0, 2, 100, 1, 19, -68};
                        log.info("server processor returns  " + Arrays.toString(messageOut));
                        exchange.getIn().setBody(messageOut, byte[].class);
                    }
                });
            }
        };
    }

}
