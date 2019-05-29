package org.traccar.protocol;

import org.junit.Test;
import org.traccar.ProtocolTest;

public class AtrackFrameDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        AtrackFrameDecoder decoder = new AtrackFrameDecoder();

        verifyFrame(
                binary("40502c414246342c3532362c3939312c3335363936313037353933313136352c313533343338313635362c313533343338313635392c313533343338313635392c2d38383432393138382c34343237313232352c37302c322c3230303536332c392c312c302c302c302c2c323030302c323030302c1a2c25434925434525434e25475125475325464c254d4c25564e25504425464325454c254554254344254154254d46254d5625425625434d25445425474c25474e254756254c43254d4525524c25525025534125534d255452254941254d502c302c3331303236302c31382c392c302c302c3254314b523332453238433730363138352c302c302c302c35342c383930313236303838313231353234373735392c3235322c36302c3132322c34312c3331303236303838313532343737352c302c687474703a2f2f6d6170732e676f6f676c652e636f6d2f6d6170733f713d34342e3237313232352c2d38382e343239313834201a2c3030393830303442303346343030393830303442303346343030393830303442303346333030393830303442303346343030393830303442303346343030393930303442303346343030393830303442303346343030393830303442303346343030393830303442303346343030393830303442303346331a2c3030343846463130303345381a2c302c3335363936313037353933313136352c302c3938302c31322c302c31382c35302c300d0a"),
                decoder.decode(null, null, binary("40502c414246342c3532362c3939312c3335363936313037353933313136352c313533343338313635362c313533343338313635392c313533343338313635392c2d38383432393138382c34343237313232352c37302c322c3230303536332c392c312c302c302c302c2c323030302c323030302c1a2c25434925434525434e25475125475325464c254d4c25564e25504425464325454c254554254344254154254d46254d5625425625434d25445425474c25474e254756254c43254d4525524c25525025534125534d255452254941254d502c302c3331303236302c31382c392c302c302c3254314b523332453238433730363138352c302c302c302c35342c383930313236303838313231353234373735392c3235322c36302c3132322c34312c3331303236303838313532343737352c302c687474703a2f2f6d6170732e676f6f676c652e636f6d2f6d6170733f713d34342e3237313232352c2d38382e343239313834201a2c3030393830303442303346343030393830303442303346343030393830303442303346333030393830303442303346343030393830303442303346343030393930303442303346343030393830303442303346343030393830303442303346343030393830303442303346343030393830303442303346331a2c3030343846463130303345381a2c302c3335363936313037353933313136352c302c3938302c31322c302c31382c35302c300d0a")));

        verifyFrame(
                binary("40502c383732442c3731322c36353232312c3335373239383037303432363439382c313533343731353836322c313533343731353836322c313533343732363437342c2d38383531303939352c34343236303637362c3137362c322c35363832372c362c312c302c302c302c2c323030302c323030302c1a2c25434925475125475325434e254345254d562553412c32392c31302c3331303236302c373838383637342c3134342c31360d0a313533343731353932312c313533343731353932322c313533343732363437342c2d38383531313032332c34343236303636382c3137362c322c35363832372c362c312c302c302c302c2c323030302c323030302c1a2c25434925475125475325434e254345254d562553412c32382c31302c3331303236302c373838383637342c3134342c31360d0a313533343731353938322c313533343731353938322c313533343732363437342c2d38383531313034362c34343236303636382c3137362c322c35363832372c362c312c302c302c302c2c323030302c323030302c1a2c25434925475125475325434e254345254d562553412c32392c392c3331303236302c373838383637342c3134342c31360d0a313533343731363034312c313533343731363034322c313533343732363437342c2d38383531313031312c34343236303636332c3137362c322c35363832372c362c312c302c302c302c2c323030302c323030302c1a2c25434925475125475325434e254345254d562553412c32392c31302c3331303236302c373838383637342c3134342c31360d0a313533343731363130322c313533343731363130322c313533343732363437342c2d38383531303938382c34343236303637362c3137362c322c35363832372c362c312c302c302c302c2c323030302c323030302c1a2c25434925475125475325434e254345254d562553412c32392c31302c3331303236302c373838383637342c3134352c31360d0a"),
                decoder.decode(null, null, binary("40502c383732442c3731322c36353232312c3335373239383037303432363439382c313533343731353836322c313533343731353836322c313533343732363437342c2d38383531303939352c34343236303637362c3137362c322c35363832372c362c312c302c302c302c2c323030302c323030302c1a2c25434925475125475325434e254345254d562553412c32392c31302c3331303236302c373838383637342c3134342c31360d0a313533343731353932312c313533343731353932322c313533343732363437342c2d38383531313032332c34343236303636382c3137362c322c35363832372c362c312c302c302c302c2c323030302c323030302c1a2c25434925475125475325434e254345254d562553412c32382c31302c3331303236302c373838383637342c3134342c31360d0a313533343731353938322c313533343731353938322c313533343732363437342c2d38383531313034362c34343236303636382c3137362c322c35363832372c362c312c302c302c302c2c323030302c323030302c1a2c25434925475125475325434e254345254d562553412c32392c392c3331303236302c373838383637342c3134342c31360d0a313533343731363034312c313533343731363034322c313533343732363437342c2d38383531313031312c34343236303636332c3137362c322c35363832372c362c312c302c302c302c2c323030302c323030302c1a2c25434925475125475325434e254345254d562553412c32392c31302c3331303236302c373838383637342c3134342c31360d0a313533343731363130322c313533343731363130322c313533343732363437342c2d38383531303938382c34343236303637362c3137362c322c35363832372c362c312c302c302c302c2c323030302c323030302c1a2c25434925475125475325434e254345254d562553412c32392c31302c3331303236302c373838383637342c3134352c31360d0a")));

        verifyFrame(
                binary("40502c373542332c3132302c37393737392c3335383930313034383039313535342c32303138303431323134323531342c32303138303431323134323531342c32303138303431333030303635352c31363432333338392c34383137383730302c3130382c322c362e352c392c302c302c302c302c302c323030302c323030302c1a0d0a"),
                decoder.decode(null, null, binary("40502c373542332c3132302c37393737392c3335383930313034383039313535342c32303138303431323134323531342c32303138303431323134323531342c32303138303431333030303635352c31363432333338392c34383137383730302c3130382c322c362e352c392c302c302c302c302c302c323030302c323030302c1a0d0a")));

        verifyFrame(
                binary("244F4B0D0A"),
                decoder.decode(null, null, binary("244F4B0D0A")));

        verifyFrame(
                binary("fe0200014104d8f196820001"),
                decoder.decode(null, null, binary("fe0200014104d8f196820001")));

        verifyFrame(
                binary("40501e58003301e000014104d8f19682525ecd5d525ee344525ee35effc88815026ab4d70000020000104403de01000b0000000007d007d000"),
                decoder.decode(null, null, binary("40501e58003301e000014104d8f19682525ecd5d525ee344525ee35effc88815026ab4d70000020000104403de01000b0000000007d007d000")));

    }

}
