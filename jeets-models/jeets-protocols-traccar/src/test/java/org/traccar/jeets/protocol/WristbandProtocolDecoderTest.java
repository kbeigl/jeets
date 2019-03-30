package org.traccar.jeets.protocol;

import org.junit.Test;
import org.traccar.jeets.ProtocolTest;
import org.traccar.jeets.protocol.WristbandProtocolDecoder;

public class WristbandProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        WristbandProtocolDecoder decoder = new WristbandProtocolDecoder(null);

        verifyNull(decoder, binary(
                "000102004159583336373535313631303030303934347c56312e307c317c7b4639312330317c30307c30307c33475f7065745f323031382f30352f31362031313a30307d0d0afffefc"));

    }

}
