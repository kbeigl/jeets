package org.jeets.itests;

import java.util.Date;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.jeets.dcs.Main;
import org.jeets.protobuf.Jeets; // Acknowledge, Device, Position
import org.jeets.protocol.util.Samples;
import org.jeets.traccar.TraccarSetup;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.traccar.model.Position;

import io.netty.buffer.ByteBufUtil;

//@RunWith(SpringJUnit4ClassRunner.class)
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = Main.class)
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
@MockEndpoints
public class DcsSpringBootIT { 
//	also see dcs DcsSpringBootTests

	@Autowired
    private ProducerTemplate client;
    @Autowired
    private ConsumerTemplate server;
    @Autowired
    private CamelContext context;
    
    @EndpointInject(uri = "mock:result")
    private MockEndpoint mockDcs;
    
    @Test
    public void testDcsOutput() throws Exception {
    	
//    	receives msgs from external source (tracker, device projects)
        context.addRoutes(new RouteBuilder() {
            public void configure() throws Exception {
                from("direct:traccar.model") // "seda:jeets-dcs"
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        Object msg = exchange.getIn().getBody();
                        Position position = (Position) msg;
                        System.out.println("received Position " + position);
                    }
                })
                .to("mock:result");
            }
        });
        
        testJeetsMessages();

//      expected Position Count !! 3 messages with 4 positions
        mockDcs.expectedMessageCount(4);
//        mockDcs.expectedMinimumMessageCount(2);
        mockDcs.assertIsSatisfied();

        List<Exchange> exchanges = mockDcs.getExchanges();
        for (Exchange exchange : exchanges) {
            Position position = (Position) exchange.getIn().getBody();
            System.out.println(position.getProtocol());
		}
    }
    
//    @Test
	public void testDcsConsumer() throws Exception {
		
		System.out.println("start testDcsConsumer ..");
//		String protocol = "ruptela";
//		int port = TraccarSetup.getProtocolPort(protocol);
		Position position = server.receiveBody("direct:traccar.model", Position.class);
		
		System.out.println(position);
		
//      Assert.assertEquals(50.35477d, position.getLatitude() , 0.00001d);
//      Assert.assertEquals( 7.59674d, position.getLongitude(), 0.00001d);
//      Assert.assertEquals("866600042245019", position.getAttributes().get("org.jeets.dcs.device.uniqueid"));
//      Assert.assertEquals("866600042245019", position.getString("org.jeets.dcs.device.uniqueid"));
//		position.getProtocol();
//		Assert.assertEquals(protocol, "ruptela");
//		Assert.assertEquals(port, position.getInteger("org.jeets.dcs.device.port"));
	}

	/**
	 * Test creation, send, receive and unmarshal ack of Jeets Protobuffer Devices.
	 */
//    @Test
//  create HEX file from dcs-traccar log and setup additional jeets file test
    private void testJeetsMessages() throws Exception {
        String protocol = "jeets";
        int port = TraccarSetup.getProtocolPort(protocol);
//      msg#1 - 1 Position
        Jeets.Device.Builder protoDeviceBuilder = Samples.createDeviceWithPositionWithOneEvent();
//      System.out.println("sending " + protoMessage);
        Jeets.Acknowledge ack = sendProtoDevice(port, protoDeviceBuilder);
//      System.out.println("received " + ack);
//      Assert.assertEquals(ack.getDeviceid(), protoDevice);
        Assert.assertEquals(ack.getDeviceid(), 123);
        
        List<Jeets.Position.Builder> posBuilderList = Samples.createSampleTrack();
        System.out.println("created list with " + posBuilderList.size() + " positions.");
//      msg#2 - 1 Position
    	posBuilderList.get(0).setFixtime(new Date().getTime());
        protoDeviceBuilder = Jeets.Device.newBuilder().setUniqueid("route");
        protoDeviceBuilder.addPosition(posBuilderList.get(0));
//      Jeets.Device protoDevice = devBuilder.build();
//      use: java.util.UUID xTraceID = java.util.UUID.randomUUID();
        System.out.println("transmitTraccarDevice: " + protoDeviceBuilder);
//      ack = TraccarSender.transmitTraccarObject(protoDevice, host, port);
        ack = sendProtoDevice(port, protoDeviceBuilder);
        System.out.println("received Acknowledge: " + ack + " at " + new Date());
//      msg#3 - 2 Positions
    	posBuilderList.get(1).setFixtime(new Date().getTime());
    	posBuilderList.get(2).setFixtime(new Date().getTime());
        protoDeviceBuilder = Jeets.Device.newBuilder().setUniqueid("route");
        protoDeviceBuilder.addPosition(posBuilderList.get(1));
        protoDeviceBuilder.addPosition(posBuilderList.get(2));
        System.out.println("transmitTraccarDevice: " + protoDeviceBuilder);
//      ack = TraccarSender.transmitTraccarObject(protoDevice, host, port);
        ack = sendProtoDevice(port, protoDeviceBuilder);
        System.out.println("received Acknowledge: " + ack + " at " + new Date());

    }

    private String host = "netty:tcp://localhost:";

    // client producer to replace tracker project!
    private Jeets.Acknowledge sendProtoDevice(int port, Jeets.Device.Builder protoDevice) {
//      requires registered ack=JeetsClientProtocol, currently as fixed @Bean
        String nettyParams = "?clientInitializerFactory=#ack&sync=true";
        Jeets.Acknowledge ack = (Jeets.Acknowledge) 
                client.requestBody(host + port + nettyParams, protoDevice);
        return ack;
    }

/*  use jeets-device and compare jeets-protocols redundant code
    private String sendHexMessage(int port, String hexMessage) {
        byte[] byteMessage = ByteBufUtil.decodeHexDump(hexMessage);
        String nettyParams = "?useByteBuf=true&allowDefaultCodec=false&producerPoolEnabled=false";
        byte[] response = client.requestBody(host + port + nettyParams, byteMessage, byte[].class);
        return ByteBufUtil.hexDump(response);
    }
 */
}
