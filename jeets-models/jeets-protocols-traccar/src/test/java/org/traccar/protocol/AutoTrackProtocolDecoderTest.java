package org.traccar.protocol;

import org.junit.Test;
import org.traccar.ProtocolTest;

public class AutoTrackProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        AutoTrackProtocolDecoder decoder = new AutoTrackProtocolDecoder(null);

        verifyNull(decoder, binary(
                "f1f1f1f1330c00201007090006de7200000000daa3"));

    }

}
