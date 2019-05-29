package org.traccar.protocol;

import org.junit.Test;
import org.traccar.ProtocolTest;

public class C2stekProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        C2stekProtocolDecoder decoder = new C2stekProtocolDecoder(null);

        verifyNull(decoder, text(
                "PA$863083038046613$D#181123#162850#1#+37.92684#+23.75933#0.62#200.1#0.0#3768#000#9#00#sz-w1001#B0907839$AP"));

        verifyPosition(decoder, text(
                "PA$353990030327618$D#091117#020928#1#22.537222#114.020948#0.00#0.0#42.1#4183#011#1#101#Wsz-wl001#B0101940#C+3.0,-5.0,+2.0$AP"));

        verifyNull(decoder, text(
                "PA$863083030602124$20$AP"));

    }

}
