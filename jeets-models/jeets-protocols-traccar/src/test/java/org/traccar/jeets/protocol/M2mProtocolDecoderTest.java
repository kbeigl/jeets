package org.traccar.jeets.protocol;

import org.junit.Test;
import org.traccar.jeets.ProtocolTest;
import org.traccar.jeets.protocol.M2mProtocolDecoder;

public class M2mProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        M2mProtocolDecoder decoder = new M2mProtocolDecoder(null);

        verifyNull(decoder, binary(
                "235A3C2A2624215C287D70212A21254C7C6421220B0B0B"));

        verifyPosition(decoder, binary(
                "A6E12C2AAADA4628326B2059576E30202A2FE85D20200B"));

    }

}
