package org.traccar.jeets;

import org.junit.Test;
import org.traccar.BaseProtocol;
import org.traccar.TrackerServer;

public class ProtobufferProtocolTest {

    /**
     * Plain java test to create (but not used yet) PipelineFactory analog to
     * ServerManager() implementation. The difference to traccar is the additional
     * Camel NettyConsumer in the BasePipelineFactory.
     */
    @Test
    public void testProtobufferProtocol() throws Exception {
        @SuppressWarnings("rawtypes")
        Class protocolClass = Class.forName("org.traccar.protocol.ProtobufferProtocol");
        Object protocolObject = protocolClass.newInstance();
        BaseProtocol protocol = (BaseProtocol) protocolObject;
        System.out.println("protocol.name: " + protocol.getName());
//      see SanavProtocol with two servers (tcp + udp)
        for (TrackerServer server : protocol.getServerList()) {
            System.out.println("TrackerServer: " + server
                    + "\nfor protocolClass: " + server.getProtocolClass()
                    + "\nwith BPFactory: " + server.getPipelineFactory()
            );
        }
    }

}
