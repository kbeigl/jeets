package org.traccar.jeets.protocol;

import org.junit.Test;
import org.traccar.jeets.ProtocolTest;
import org.traccar.jeets.protocol.NavigilProtocolDecoder;

public class NavigilProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        NavigilProtocolDecoder decoder = new NavigilProtocolDecoder(null);

        verifyNull(decoder, binary(
                "01004300040020000000f60203080200e7cd0f510c0000003b00000000000000"));

        verifyPosition(decoder, binary(
                "0100b3000f0024000000f4a803080200ca0c1151ef8885f0b82e6d130400c00403000000"));

    }

}
