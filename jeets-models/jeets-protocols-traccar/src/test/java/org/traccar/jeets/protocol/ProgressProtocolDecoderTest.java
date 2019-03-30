package org.traccar.jeets.protocol;

import org.junit.Test;
import org.traccar.jeets.ProtocolTest;
import org.traccar.jeets.protocol.ProgressProtocolDecoder;

public class ProgressProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        ProgressProtocolDecoder decoder = new ProgressProtocolDecoder(null);

        verifyNull(decoder, binary(
                "020037000100000003003131310f003335343836383035313339303036320f00323530303136333832383531353535010000000100000000000000e6bb97b6"));

    }

}
