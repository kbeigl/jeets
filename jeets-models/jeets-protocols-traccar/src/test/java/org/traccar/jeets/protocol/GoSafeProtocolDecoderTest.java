package org.traccar.jeets.protocol;

import org.junit.Test;
import org.traccar.jeets.ProtocolTest;
import org.traccar.jeets.protocol.GoSafeProtocolDecoder;

public class GoSafeProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        GoSafeProtocolDecoder decoder = new GoSafeProtocolDecoder(null);

        verifyPositions(decoder, text(
                "*GS06,359568052570135,090349191018,,SYS:G3C;V1.40;V1.0.4,GPS:A;10;S26.112722;E28.078766;0;23;1581;0.80,COT:,ADC:10.86;3.79,DTT:4000;E7;0;0;0;1$090419191018,,SYS:G3C;V1.40;V1.0.4,GPS:A;10;S26.112722;E28.078766;0;23;1581;0.80,COT:,ADC:10.85;3.79,DTT:4000;E7;0;0;0;1#"));

        verifyPositions(decoder, text(
                "*GS06,359913060650380,152248050718,,SYS:G3C;V1.38;V05,GPS:A;10;N31.914370;E35.914640;0;0,COT:689,ADC:0.18;3.55,DTT:4025;E6;0;0;0;1#"));

        verifyPositions(decoder, text(
                "*GS06,359913060650380,101019050718,,SYS:G3C;V1.38;V05,GPS:L;6;N31.916576;E35.908480;0;0,GSM:1;4;416;3;627A;A84B;-66,COT:188,ADC:4.31;3.88,DTT:4005;E6;0;0;0;1#"));

        verifyPositions(decoder, text(
                "*GS06,860078024287174,070120310318,,SYS:G3SC;V3.32;V1.1.8,GPS:A;9;N23.169946;E113.450568;0;0;23;0.86,COT:65;20,ADC:4.27;3.73;0.01;0.02,DTT:4004;E0;0;0;0;1,IWD:0;0;000000000000#"));

        verifyPositions(decoder, text(
                "*GS06,860078024213915,032544190318,,SYS:G3SC;V3.32;V1.1.8,GPS:A;7;N3.052417;E101.787112;0;0;94;1.38,COT:686;0-0-0,ADC:16.25;4.09,DTT:4000;E0;0;0;0;1#"));

        verifyPositions(decoder, text(
                "*GS06,351535058659335,062728190318,,SYS:G6S;V3.32;V1.0.5,GPS:A;10;N23.169806;E113.450760;0;0;81;0.77,COT:0,ADC:0.00;0.16,DTT:80;E0;0;0;0;1#"));

        verifyPositions(decoder, text(
                "*GS26,356449061046586,082522030117,,SYS:G737IC;V1.13;V1.0.5,GPS:V;5;N42.594136;W70.723832;0;0;8;2.06,GSM:;;310;260;C76D;9F1D;-85,ADC:3.86,DTT:3918C;;0;0;0;1,#"));

        verifyPositions(decoder, text(
                "*GS56,357330051092344,123918301116,10,GPS:L;9;N47.582920;W122.238720;0;0;102;0.99,GSM:0;0;310;410;A7DB;385C;-86,COT:76506,ADC:0.82;3.77,DTT:2184;;0;0;10000;0$000000000000,86,GPS:A;6;N47.582912;W122.238840;0;0;88;2.20,COT:76506,ADC:0.00;3.75,DTT:0;;0;0;40;0$000000000000,86,GPS:A;6;N47.582912;W122.238840;0;0;88;2.20,COT:76506,ADC:0.00;3.74,DTT:0;;0;0;40;0$000000000000,93,GPS:A;6;N47.582912;W122.238840;0;0;88;2.20,COT:76506,ADC:0.00;3.73,DTT:8000;;0;0;80000;0$000000000000,13,GPS:L;6;N47.582912;W122.238840;0;0;88;2.20,COT:76506,ADC:11.09;3.79,DTT:2004;;0;0;80000;0$000000000000,90,GPS:L;6;N47.582912;W122.238840;0;0;88;2.20,COT:76506,ADC:11.13;3.79,DTT:23004;;0;0;10000;0$000000000000,,GPS:L;6;N47.582912;W122.238840;0;0;88;2.20,GSM:5;2;310;410;A7DB;385C;-89,COT:76506,ADC:14.12;3.81,DTT:23184;;0;0;0;6#"));

        verifyPositions(decoder, text(
                "*GS26,356449061139936,022918011216,,SYS:G737IC;V1.13;V1.0.5,GPS:A;9;N42.651728;W70.623520;0;0;48;1.50,ADC:4.08,DTT:3900C;;0;0;0;1,#"));

        verifyNotNull(decoder, text(
                "*GS56,356449063230915,052339180916,,SYS:G7S;V1.08;V1.2,GPS:V;4;N24.730006;E46.637816;14;0;630,GSM:;;420;4;5655;507A;-70,COT:75242;2-8-17,ADC:13.22;0.08,DTT:23004;;0;0;0;1#"));

        verifyNotNull(decoder, text(
                "*GS56,356449063230915,052349180916,,SYS:G7S;V1.08;V1.2,GPS:V;6;N24.730384;E46.637620;47;56;607,GSM:;;420;4;5655;507A;-70,COT:75290;2-8-27,ADC:13.24;0.08,DTT:23004;;0;0;0;1#"));

        verifyNotNull(decoder, text(
                "*GS56,356449063230915,052444180916,,SYS:G7S;V1.08;V1.2,GPS:V;6;N24.730384;E46.637620;47;56;607,GSM:;;420;4;5655;F319;-102,COT:75290;2-9-27,ADC:13.00;0.08,DTT:23004;;0;0;0;1$052449180916,,SYS:G7S;V1.08;V1.2,GPS:V;6;N24.730384;E46.637620;47;56;607,GSM:;;420;4;5655;F319;-102,COT:75290;2-9-27,ADC:13.13;0.08,DTT:23004;;0;0;0;1#"));

        verifyPositions(decoder, text(
                "*GS16,356449062643845,141224290316,,SYS:G79;V1.13;V1.0.2,GPS:V;5;N24.694972;E46.680736;46;334;606;1.43,GSM:;;420;4;5655;4EB8;-57,COT:330034,ADC:13.31;3.83,DTT:27004;;0;0;0;1,OBD:064101000400000341057E04410304000341510104411001C203410F4B0341112904411F01AB0641010004000014490201FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF03410D21,FUL:28260"));

        verifyPositions(decoder, text(
                "*GS16,351535059439208,145425130316,,GPS:V;0;N0.000000;E0.000000;0;0;0;0.00;0.00,GSM:1;3;416;3;A8C;2820;-81;416;3;A8C;281F;-83;416;3;A8C;368A;-87;416;3;A8C;368B;-89;416;3;A8C;2C26;-103;416;3;A8C;3689;-107;416;3;A8C;2D83;-107"));

        verifyPosition(decoder, text(
                "*GS02,358696043774648,GPS:230040;A;S1.166829;E36.934287;0;0;170116,STT:20;0,MGR:32755204,ADC:0;11.2;1;28.3;2;4.1,GFS:0;0"));

        verifyNull(decoder, text(
                "*GS02,358696043774648"));

        verifyPositions(decoder, text(
                "*GS16,351535058709775,100356130215,,SYS:G79W;V1.06;V1.0.2,GPS:A;6;N24.802700;E46.616828;0;0;684;1.35,COT:60,ADC:4.31;0.10,DTT:20000;;0;0;0;1"));

        verifyPositions(decoder, text(
                "*GS16,351535059439208,074558291015,,GPS:A;9;N31.935942;E35.867092;;345;921;1.03;1.59,GSM:1;3;416;3;A8C;368B;-78;416;3;A8C;2820;-73;416;3;BB8;2CBE;-76;416;3;A8C;368A;-76;416;3;A8C;2C26;-79,OBD:04410C122003410D0F03411C0103410547037F011203411100"));

        verifyPositions(decoder, text(
                "*GS16,351535059439208,083515281015,,GPS:A;9;N31.959502;E35.908316;;108;890;1.05;1.79,GSM:1;4;416;3;AF0;A3A6;-59;416;3;AF0;A3A3;-50;416;3;AF0;A3A4;-56;416;3;AF0;A3A5;-62;416;3;AF0;B195;-76,OBD:04410C194603410D2303411C0103410583037F011203411115"));

        verifyNull(decoder, text(
                "*GS16,351535058709775"));
        
        verifyPositions(decoder, text(
                "*GS16,351535059439208,103441131015,,GPS:A;8;N31.960122;E35.921652;27;99;847;1.33;2.41,GSM:1;4;416;3;AF0;9C73;-61;416;3;AF0;9C89;-68,OBD:04410C0DA403410D0B03411C010341057A037F011203411100$103453131015,,GPS:A;8;N31.959976;E35.922144;6;0;835;1.33;2.41,GSM:1;4;416;3;AF0;9C73;-67;416;3;AF0;9C89;-64;416;3;AF0;B389;-83,OBD:04410C0D8E03410D0B03411C010341057D037F011203411100$103503131015,,GPS:A;9;N31.959870;E35.922284;11;127;830;1.33;2.41,GSM:1;4;416;3;AF0;9C73;-67;416;3;AF0;9C89;-64;416;3;AF0;B389;-83,OBD:04410C0D8E03410D0B03411C010341057D037F011203411100$103513131015,,GPS:A;9;N31.959742;E35.922516;10;106;830;1.37;2.91,GSM:1;4;416;3;AF0;9C73;-67;416;3;AF0;9C89;-64;416;3;AF0;B389;-83,OBD:04410C0D1003410D0603411C010341057E037F011203411100$103553131015,,GPS:A;8;N31.959564;E35.923308;6;0;836;1.41;2.43,GSM:1;4;416;3;AF0;9C73;-65;416;3;AF0;B389;-71;416;3;AF0;9C89;-74,OBD:04410C0DAE03410D0403411C010341057C037F011203411100#"));

        verifyPositions(decoder, text(
                "*GS16,351535059439208,155750220815,,SYS:G79;V1.10;V1.0.2,GPS:A;4;N31.944198;E35.846644;0;0;923;9.47;1.00,COT:155133,ADC:12.21;0.10,DTT:20002;;0;0;0;1#"));
        
        verifyPositions(decoder, text(
                "*GS16,351535059439208,070034220815,,SYS:G79;V1.10;V1.0.2,GPS:A;8;N31.945970;E35.859848;29;65;922;1.14;1.68,COT:147528,ADC:14.07;0.11,DTT:27006;;0;0;0;3,OBD:04410C1ECD03410D2D03411C010341057A037F011203411107$070035220815,,SYS:G79;V1.10;V1.0.2,GPS:A;8;N31.945934;E35.859908;29;86;922;1.14;1.68,COT:147528,ADC:13.94;0.15,DTT:27006;;0;0;0;3,OBD:04410C1ECD03410D2D03411C010341057A037F011203411107$070037220815,,SYS:G79;V1.10;V1.0.2,GPS:A;8;N31.945844;E35.859952;29;123;922;1.14;1.68,COT:147625,ADC:13.75;0.11,DTT:27006;;0;0;0;3,OBD:04410C0FE803410D1803411C010341057C037F011203411100$070038220815,,SYS:G79;V1.10;V1.0.2,GPS:A;8;N31.945808;E35.859940;29;145;923;1.14;1.68,COT:147625,ADC:14.00;0.11,DTT:27006;;0;0;0;3,OBD:04410C0FE803410D1803411C010341057C037F011203411100#"));

    }

}
