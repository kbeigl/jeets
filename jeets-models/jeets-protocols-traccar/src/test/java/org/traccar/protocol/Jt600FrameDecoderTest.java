package org.traccar.protocol;

import org.junit.Test;
import org.traccar.jeets.ProtocolTest;
import org.traccar.protocol.Jt600FrameDecoder;

public class Jt600FrameDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        Jt600FrameDecoder decoder = new Jt600FrameDecoder();

        verifyFrame(
                binary("24315011626912001b21111718095900000000000000000e0000005c000000000000000000"),
                decoder.decode(null, null, binary("24315011626912001b21111718095900000000000000000e0000005c00000000000000000024315011626912001b22111708130400000000000000000e0000005a00000000000000000024315011626912001b22111708140400000000000000000e0000005a000000723e18a61b01")));

        verifyFrame(
                binary("2475604055531611002311111600311326144436028210791d016c0000001f070000000020c03c4f6d07d80ccf"),
                decoder.decode(null, null, binary("2475604055531611002311111600311326144436028210791d016c0000001f070000000020c03c4f6d07d80ccf")));

        verifyFrame(
                binary("2475605035891613002328091601152806086750106533350c00000000000a000000000000e1ff4f97007f1607"),
                decoder.decode(null, null, binary("2475605035891613002328091601152806086750106533350c00000000000a000000000000e1ff4f97007f1607")));

        verifyFrame(
                binary("28333132303832303032392C5730312C30323535332E333535352C452C323433382E303939372C532C412C3137313031322C3035333333392C302C382C32302C362C33312C352C32302C323029"),
                decoder.decode(null, null, binary("28333132303832303032392C5730312C30323535332E333535352C452C323433382E303939372C532C412C3137313031322C3035333333392C302C382C32302C362C33312C352C32302C323029")));

        verifyFrame(
                binary("24312082002911001B171012053405243809970255335555000406140003EE2B91044D1F02"),
                decoder.decode(null, null, binary("24312082002911001B171012053405243809970255335555000406140003EE2B91044D1F02")));

        verifyFrame(
                binary("28373536303430353535332c404a5429"),
                decoder.decode(null, null, binary("28373536303430353535332c404a5429")));

    }

}
