package org.traccar.protocol;

import org.junit.Test;
import org.traccar.jeets.ProtocolTest;
import org.traccar.protocol.EgtsFrameDecoder;

public class EgtsFrameDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        EgtsFrameDecoder decoder = new EgtsFrameDecoder();

        verifyFrame(
                binary("0100020B0025003A5701C91A003A5701CD6E68490202101700CBB4740F7617FD924364104F116A0000000000010300001EC2"),
                decoder.decode(null, null, binary("0100020B0025003A5701C91A003A5701CD6E68490202101700CBB4740F7617FD924364104F116A0000000000010300001EC2")));

    }

}
