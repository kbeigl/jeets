package org.jeets.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.traccar.BaseProtocol;
import org.traccar.TrackerServer;

public class ProtobufferProtocolTest {

    /**
     * Plain java test to create (but not used yet) PipelineFactory analog to
     * ServerManager() implementation. Context.init will be done via Camel Route.
     */
    @Test
    public void testProtobufferProtocol() throws Exception {
        
        org.traccar.Context.init("setup/traccar.xml");
        String protocolString = "org.traccar.protocol.ProtobufferProtocol";
        
        @SuppressWarnings("rawtypes")
        Class protocolClass = Class.forName(protocolString);
        Object protocolObject = protocolClass.newInstance();
        BaseProtocol protocol = (BaseProtocol) protocolObject;
        System.out.println("protocol.name: " + protocol.getName());
        for (TrackerServer server : protocol.getServerList()) {
            System.out.println("TrackerServer: " + server
                    + "\nfor protocolClass: " + server.getProtocolClass()
                    + "\nwith BasePipelineFactory: " + server.getPipelineFactory()
            );
            assertEquals(protocolString, server.getProtocolClass().getCanonicalName());
        }
    }

}
