package org.traccar.protocol;

import org.junit.Test;
import org.traccar.jeets.ProtocolTest;
import org.traccar.protocol.HuabaoFrameDecoder;

import static org.junit.Assert.assertEquals;

public class HuabaoFrameDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        HuabaoFrameDecoder decoder = new HuabaoFrameDecoder();

        assertEquals(
                binary("7e307e087d557e"),
                decoder.decode(null, null, binary("7e307d02087d01557e")));

    }

}
