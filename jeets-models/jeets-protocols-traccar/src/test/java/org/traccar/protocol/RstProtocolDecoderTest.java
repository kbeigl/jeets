package org.traccar.protocol;

import org.junit.Test;
import org.traccar.ProtocolTest;
import org.traccar.model.Position;

public class RstProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        RstProtocolDecoder decoder = new RstProtocolDecoder(null);

        verifyPosition(decoder, text(
                "RST;L;RST-MINIv2;V7.02;008068078;61;1;27-01-2020 21:36:33;27-01-2020 21:36:33;-16.696159;-49.284275;0;67;786;1;15;0;00;B0;00;19;06;12.42;4.16;79;20;FE;0000;01;E0;00800020;0;467;FIM;"));

        verifyAttribute(decoder, text(
                "RST;A;RST-MINIv2;V7.00;008033985;1;7;30-08-2019 11:31:38;30-08-2019 11:31:15;-23.645868;-46.637741;0;226;828;0;10;0;00;20;00;1A;02;0.02;3.40;0;0;FE;0000;04;80;11;0;FIM;"),
                Position.KEY_BATTERY, 3.40);

        verifyPosition(decoder, text(
                "RST;A;RST-MINIv2;V7.00;008033985;1;7;30-08-2019 11:31:38;30-08-2019 11:31:15;-23.645868;-46.637741;0;226;828;0;10;0;00;20;00;1A;02;0.02;3.40;0;0;FE;0000;04;80;11;0;FIM;"));

        verifyPosition(decoder, text(
                "RST;A;RST-MINIv2;V7.00;008033985;6;47;30-08-2019 19:01:13;30-08-2019 19:01:14;-23.645851;-46.637817;0;294;811;1;11;0;00;30;00;1A;02;3.82;4.16;0;0;FE;0000;02;40;71;000001F60A55;FIM;"));

    }

}
