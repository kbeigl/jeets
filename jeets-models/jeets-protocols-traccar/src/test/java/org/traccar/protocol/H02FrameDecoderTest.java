package org.traccar.protocol;

import org.junit.Test;
import org.traccar.ProtocolTest;
import org.traccar.protocol.H02FrameDecoder;

import static org.junit.Assert.assertEquals;

public class H02FrameDecoderTest extends ProtocolTest {

    @Test
    public void testDecodeShort() throws Exception {

        H02FrameDecoder decoder = new H02FrameDecoder(0);

        assertEquals(
                binary("2a48512c3335353438383032303131333931312c56312c3031323934352c412c353233312e37393238332c4e2c30313332342e31303731382c452c302e30352c302c3137303231372c464646464646464623"),
                decoder.decode(null, null, binary("2a48512c3335353438383032303131333931312c56312c3031323934352c412c353233312e37393238332c4e2c30313332342e31303731382c452c302e30352c302c3137303231372c4646464646464646230d0a")));

        assertEquals(
                binary("2441060116601245431311165035313006004318210e000000fffffbffff0024"),
                decoder.decode(null, null, binary("2441060116601245431311165035313006004318210e000000fffffbffff0024")));

        assertEquals(
                binary("2441060116601245431311165035313006004318210e000000fffffbffff0024"),
                decoder.decode(null, null, binary("2441060116601245431311165035313006004318210e000000fffffbffff00242a48512c343130363031313636302c56312c3132343535322c412c353033352e333132392c4e2c30303433312e383231312c452c3030302e32302c3030302c3133313131362c464646464642464623")));

        assertEquals(
                binary("2a48512c3335333538383036303031353536382c56312c3139333530352c412c3830392e303031302c532c333435342e383939372c572c302e30302c302e30302c3239313031332c65666666666266662c3030303264342c3030303030622c3030353338352c3030353261612c323523"),
                decoder.decode(null, null, binary("2a48512c3335333538383036303031353536382c56312c3139333530352c412c3830392e303031302c532c333435342e383939372c572c302e30302c302e30302c3239313031332c65666666666266662c3030303264342c3030303030622c3030353338352c3030353261612c323523")));

        assertEquals(
                binary("24430025645511183817091319355128000465632432000100ffe7fbffff0000"),
                decoder.decode(null, null, binary("24430025645511183817091319355128000465632432000100ffe7fbffff0000")));

    }

    @Test
    public void testDecodeLong() throws Exception {

        H02FrameDecoder decoder = new H02FrameDecoder(0);

        assertEquals(
                binary("24410600082621532131081504419390060740418306000000fffffbfdff0015060000002c02dc0c000000001f"),
                decoder.decode(null, null, binary("24410600082621532131081504419390060740418306000000fffffbfdff0015060000002c02dc0c000000001f")));

    }

    @Test
    public void testDecodeAlternative() throws Exception {

        H02FrameDecoder decoder = new H02FrameDecoder(0);

        assertEquals(
                binary("2a48512c343230363131393133302c4e42522c3130323430332c3233382c312c302c372c313131312c323236342c36332c313131312c323236352c35382c313131312c323236362c35302c313131312c333133352c33372c313131312c3630352c33332c313131312c343932302c33302c313131312c3630372c32382c3131303131372c46464646444646462c3623"),
                decoder.decode(null, null, binary("2a48512c343230363131393133302c4e42522c3130323430332c3233382c312c302c372c313131312c323236342c36332c313131312c323236352c35382c313131312c323236362c35302c313131312c333133352c33372c313131312c3630352c33332c313131312c343932302c33302c313131312c3630372c32382c3131303131372c46464646444646462c3623")));

        assertEquals(
                binary("2442061191301024031101175540227006012321670c000095fffffbffff001f00000001f800ee010000000032"),
                decoder.decode(null, null, binary("2442061191301024031101175540227006012321670c000095fffffbffff001f00000001f800ee010000000032")));

        assertEquals(
                binary("5800009814991024031101175540227006012321670c000095fffffbffff0033"),
                decoder.decode(null, null, binary("5800009814991024031101175540227006012321670c000095fffffbffff0033")));

        assertEquals(
                binary("2a48512c343230363131393133302c4e42522c3130323431362c3233382c312c302c372c313131312c323236342c35332c313131312c323236352c36302c313131312c323236362c34342c313131312c333133352c34332c313131312c3630352c33392c313131312c343932302c32392c313131312c3630372c32342c3131303131372c46464646464246462c3623"),
                decoder.decode(null, null, binary("2a48512c343230363131393133302c4e42522c3130323431362c3233382c312c302c372c313131312c323236342c35332c313131312c323236352c36302c313131312c323236362c34342c313131312c333133352c34332c313131312c3630352c33392c313131312c343932302c32392c313131312c3630372c32342c3131303131372c46464646464246462c3623")));

    }

}
