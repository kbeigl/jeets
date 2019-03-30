package org.traccar.jeets.protocol;

import org.junit.Test;
import org.traccar.jeets.ProtocolTest;
import org.traccar.jeets.protocol.KenjiProtocolDecoder;

public class KenjiProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        KenjiProtocolDecoder decoder = new KenjiProtocolDecoder(null);

        verifyPosition(decoder, text(
                ">C800000,M005004,O0000,I0002,D124057,A,S3137.2783,W05830.2978,T000.0,H254.3,Y240116,G06*17"),
                position("2016-01-24 12:40:57.000", true, -31.62131, -58.50496));
    }    

}
