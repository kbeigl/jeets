package org.jeets.device.console;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.camel.CamelContext;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.netty.NettyComponent;
import org.apache.camel.component.netty.NettyConfiguration;
import org.apache.camel.component.netty.NettyEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.SynchronizationAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBufUtil;

/**
 * This Device is basically a wrapper for the camel-netty4 ProducerTemplate
 * acting as the TCP client interacting with the server.
 * <p>
 * This Device is focused on sending and receiving plain byte[] arrays, i.e.
 * plain TCP communication. The Raw Device can serve as a generic client to send
 * (and receive) byte[] arrays regardless of their proprietary protocol format.
 * <p>
 * Camel and ProducerTemplate are hidden in the Device, which can be handled as
 * a regular Java Object. Neither is Spring used for the Device.
 */
/* Developers Note
 * 1. This explicit Device was only created as the xml route 
 *    <to uri="netty4:tcp://localhost:5046?sync=true&amp;useByteBuf=true&amp;synchronous=true&amp;reuseChannel=true" />
 *    <log message="RECEIVED: ${body}"/>
 *    DOES NOT RETURN ACK while server receives and responds.
 * 2. The Callback was explicitly coded for testing and it should reduce the code 
 *    significantly by applying a regular call and wait for the future to return.
 */
public class Device {   
    // maybe (currently only TCP) HexDevice ?!
    private static final Logger log = LoggerFactory.getLogger(Device.class);

    private static CamelContext context;

    private ProducerTemplate device;
    NettyEndpoint serverEndpoint;
    /** A Device can run as one-way (false) OR request-response (true) at one time only */
    private boolean isRequest;  // final?
    private String host;
    private int port;
    private ExecutorService executorService;

    /**
     * Create a new Device with server host:port to send byte arrays to.
     * <p>
     * For one-way communication (isRequest=false) the send*Message methods will be
     * queued and sent whenever a connection is available. In request-response
     * communication every message has to be sent individually in order to process
     * the ACK before sending the next message.
     * 
     * @param host
     * @param port
     * @param isRequest
     * @throws Exception
     */
    public Device( String host, int port, boolean isRequest ) throws Exception {
        this( host, port, isRequest, null );
    }

    /**
     * Without explicit {@link CamelContext} the first Device of an application
     * creates a context for all subsequent devices. By passing an existing context
     * at creation of the first Device it will be used for further devices.
     */
    public Device( String host, int port, boolean isRequest, CamelContext camelContext ) throws Exception {
        this.host = host;
        this.port = port;
        this.isRequest = isRequest;

        executorService = Executors.newSingleThreadExecutor();

        Device.context = camelContext;
        initCamel();
        setServerEndpoint();
    }

    /**
     * Set the server (at Device creation) to send messages to.
     */
    private void setServerEndpoint( /* implies host, port */ ) {
        
//      later all options can be controlled via Spring Boot with camel-netty-starter 
//      to have support for auto configuration. The component supports 79 options !
        NettyConfiguration nettyConfig = new NettyConfiguration();
//      netty4:tcp://localhost:3001
        nettyConfig.setProtocol("tcp");
        nettyConfig.setHost(host);
        nettyConfig.setPort(port);

//      implicitly set for Netty producer = client ?
//      nettyConfig.setClientMode(true);

//      does not seem to reconnect after initial Transmission failure
//      nettyConfig.setReconnect(true);     behavior unclear

        /* In request only mode without acknowledgement some devices expect an
         * identification message to open the socket connection for following messages.
         * By disabling the pool the device will send subsequent messages via the same
         * socket connection, i.e. port. With the pool the id message is sent to one
         * channel and the following message will fail in a new channel ... */
        nettyConfig.setProducerPoolEnabled(false);
        /* one-way or request-response */
        nettyConfig.setSync(isRequest);
//      unclear, but required: to investigate thoroughly
        nettyConfig.setUseByteBuf(true);

//      see ptc.cfg files with de.ptc.server.reuseAdress etc. and de.ptc.server.CHILD ...
//      nettyConfig.setReuseChannel(true);      // default false
//      nettyConfig.setDisconnect(false);       // default false
//      nettyConfig.setKeepAlive(true);         // default true
        
//      Time to wait for a socket connection to be available [millis].
//      Allows to use a timeout for the Netty producer when calling a remote server. 
//      The requestTimeout is using Netty’s ReadTimeoutHandler to trigger the timeout.
//      Currently the reconnection is programmed in the MessageLoop.
//      nettyConfig.setConnectTimeout(10000);   // default 10000 ms = 10 sec);

        NettyComponent netty4 = context.getComponent("netty4", NettyComponent.class);
//      [main] DEBUG DefaultComponent + Cannot resolve property placeholders on component: 
//         org.apache.camel.component.netty4.NettyComponent@9f46d94 as PropertiesComponent is not in use  ???
//      netty4.setConfiguration(nettyConfig);

        serverEndpoint = new NettyEndpoint(null, netty4, nettyConfig);
    }

    /**
     * As a first approach the first Device creates an internal static CamelContext
     * and every subsequent Device will use these.
     */
    private void initCamel() throws Exception {
//      currently without Registry (for potential ClientInitFactory)
        if (context == null) {
//          context = new DefaultCamelContext(registry);
            context = new DefaultCamelContext();
            ((DefaultCamelContext) context).setName("camel-devices");
//          this doesn't do any harm, if already started
//          improve with if !context.getStatus().isStarted()
            context.start();
        }

        device = context.createProducerTemplate();
    }

    /**
     * Convenience method to convert hex message to byte[] message and send it to
     * the server.
     * <p>
     * see {@link #sendByteMessage} for details
     */
    public String sendHexMessage( String hexString ) throws Throwable {
        log.info("input \"" + hexString + "\"");
        if (!isRequest) {
            sendByteMessage(decodeHexDump(hexString));
            return null;
        } else {    // isRequest
            byte[] byteMessage = decodeHexDump(hexString);
            log.info("sendByteMessage \"" + Arrays.toString( byteMessage ) + "\"");
            byte[] response = sendByteMessage( byteMessage );
            String hexResponse = ByteBufUtil.hexDump(response);
            log.info("response \"" + Arrays.toString( response ) + "\"");
            log.info("output \"" + hexResponse + "\"");
            return hexResponse;
        }
    }

    /**
     * The 'send-' methods are 'request only' or 'fire and forget'. The caller sends
     * a message and can not expect a reply. Therefore the method returns void, i.e.
     * continues or throws an Exception.
     * <p>
     * see {@link #asyncSendByteMessage} for details
     * 
     * @param byteMessage
     * @throws Throwable
     */
    public byte[] sendByteMessage(byte[] byteMessage) throws Throwable {

        if (!isRequest) {
            log.debug("queuing " + Arrays.toString(byteMessage));
//          messageQueue.add(byteMessage);
            messageQueue.put(byteMessage);  // blocks
//          messageQueue.offer(byteMessage);

            if (!messageLoopRunning) {
//              difference ?
//              executorService.execute(messageLoop);
                executorService.submit(messageLoop);
            }
            return null; // fire and forget, message can be lost
        } else {    // isRequest
//          first approach: direct call from producer with wait
            byte[] response = null;
            
//          temporary test - hangs ;)
//          test serverUri with Camel some uri "direct:whatever"
            String serverUri = "netty4:tcp://localhost:5046"
//                  + "?sync=true"
//                  + "&disconnect=false"
//                  + "&reuseChannel=true"
//                  + "&requestTimeout=2000"    // [ms] handle with care! Used to terminate test ONLY!
//                  + "&producerPoolEnabled=false"
//                  + "&useByteBuf=true"
            ;
            
            try {
                response = device.requestBody(serverEndpoint, byteMessage, byte[].class);
//                response = device.requestBody(serverUri, byteMessage, byte[].class);
            } catch (CamelExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return response;
        }

//      apply (where?) ScheduledExecutorService later to serve time stamps
    }
    
/*  TODO: request-response with ProtoBuffer as reference
    public byte[] requestBody(byte[] bytes) throws .. {
    public void queueHex MessageRequest( String hexString ) throws Throwable {
    public void queueByteMessageRequest( byte[] bytes ) throws Throwable {
 */
    
    private boolean messageLoopRunning = false;

    /**
     * Tracker's internal FIFO storage for buffering messages to be sent.
     * <p>
     * In the real world a Tracker collects (GPS Unit) and sends (GSM Unit) messages
     * separately. In a tunnel usually both units can not operate. Therefore most
     * devices have an internal storage for unsent messages. The device should only
     * remove messages after they have been sent to the server, which can actually
     * only be achieved with some Acknowledge message.
     */
    private BlockingQueue<byte[]> messageQueue = new LinkedBlockingQueue<byte[]>();

    /**
     * If transmission fails, try to reconnect after every {@link #reconnectTimeout}
     * seconds to continue sending messages.
     */
//  TODO automate 5, 10, 30, 60 seconds (see Tracker xy specs)
    private int reconnectTimeout = 5;

    private SendMessageLoop messageLoop = new SendMessageLoop();

    /**
     * All Messages are collected in a MessageQueue and the MessageLoop takes care
     * of sending them whenever connectivity allows it.
     */
    private class SendMessageLoop implements Runnable {
//      TODO: add device.cancelTransmission, timeOutMillis !! or nrOfRetries .. ?
//        control timing of messages externally, i.e. sendInterval = 1 second
//        IF the connection is held .. validate !
        public void run() {
            messageLoopRunning = true;
            while (!messageQueue.isEmpty()) {
                log.debug( messageQueue.size() + " messages queued: " + messageQueue.toString());
                byte[] byteMessage = messageQueue.peek();
                try {
                    asyncSendByteMessage(byteMessage);
//                  success: remove sent message, proceed to next, else catch
                    messageQueue.take(); // could block, logically should not
                } catch (Throwable e) {
                    log.warn("Transmission failed: " + e.getMessage());
                    log.info("Trying again in " + reconnectTimeout + " seconds ...");
                    delay(reconnectTimeout * 1000);
                }
            }
            messageLoopRunning = false;
        }
    }

    /**
     * Blocking queue to synchronize sending and callback.
     * <p>
     * The queue is restricted to a single Object, which can be the actual byte[]
     * message or a Throwable from the callback.
     * <p>
     * Note that the BlockingQueue was spontaneously chosen as 'some' blocking
     * object. This could be replaced with a dedicated blocking object for this very
     * purpose. See Data class at www.baeldung.com/java-wait-notify to mimic an
     * Exchange with ex.in = message and ex.out = Throwable or equals(ex.in)
     * with SendMessageLoop = sender and SentCallback = receiver.
     */
    private BlockingQueue<Object> callbackQueue = new LinkedBlockingQueue<Object>(1);

    /**
     * Send a message as byte[] array, wait to establish connection and do not
     * expect a reply.
     * <p>
     * This is the least secure communication, since the server does not acknowledge
     * the reception of a message. Vice versa the sender can not do its house
     * keeping of removing sent messages. Therefore a callback from Camel will
     * inform about the result of sending the message: After successful transmission
     * the method will return, on failure it throws an Exception retrieved from
     * Netty.
     * <p>
     * Note that the 'send-' methods do not refer to TCP, i.e. Netty connections,
     * but to Camel Routes from netty producer to consumer (server). Camel's
     * SynchronizationAdapter waits for the completion and can access the Exchange
     * in the onDone method to check, if the request was successful (onComplete) or
     * not (onFailure). This method is called from the MessageLoop Thread, while the
     * callback is caught in a different Thread.
     * 
     * @return If method returns (after blocking) it was successful, else: throws ..
     * @throws Throwable if problems occur message is considered 'not delivered'
     */
    private void asyncSendByteMessage(byte[] bytes) throws Throwable {
        callbackQueue.clear();  // assertion to be removed
        log.info("sending ByteMessage " + Arrays.toString(bytes));
//      1. with callback returns after 3367 [ms] (most complicated)
//      CompletableFuture<Object> future =
        device.asyncCallbackSendBody(serverEndpoint, bytes, callback);
//      2. without callback returns after 3841 [ms]
//      device.sendBody(serverEndpoint, bytes);
//      3./4. = 1./2. with future waiting
//      future.get() .. Throws:
//      CancellationException - if this future was cancelled
//         ExecutionException - if this future completed exceptionally
//       InterruptedException - if the current thread was interrupted while waiting
        Object returnValue = null;
        try {
//          block and wait
            returnValue = callbackQueue.take();
//          log.debug("received: " + returnValue);
            if (returnValue.equals(bytes))
                return;
            else // add typing to concrete Exceptions
                if (returnValue instanceof Throwable)
                    throw (Throwable) returnValue;

        } catch (InterruptedException ie) {
            log.warn("callbackQueue was interrupted !?");
            throw ie;
        }
    }

    /** Single instance for all queued consecutive calls! */
    private SentCallback callback = new SentCallback();

    /**
     * This callback will put the returnValue in the callbackQueue, being the
     * original byte[] message to indicate success or a Throwable which can be
     * handled externally. Since we don't expect a return message it can be
     * disposed.
     */
    private class SentCallback extends SynchronizationAdapter {
        private boolean success = false;

        @Override
        public void onComplete(Exchange exchange) {
            success = true;
//          log.debug("Callback completed!");
            super.onComplete(exchange);
        }

        @Override
        public void onFailure(Exchange exchange) {
            success = false;
//          log.debug("Callback failed!");
            super.onFailure(exchange);
        }

        /** completion will be called in any case - successful or not.  */
        @Override
        public void onDone(Exchange exchange) {
            callbackQueue.clear();  // assertion to be removed
            Object returnValue;
            if (success) {
//              for a one-way message getIn is untouched (echo)
                returnValue = exchange.getIn().getBody();
                log.debug("Callback returns: " + ByteBufUtil.hexDump((byte[]) returnValue));
            } else {
                returnValue = exchange.getException();
            }

            try {
                callbackQueue.put(returnValue);
                log.debug("Callback returns: " + returnValue);
            } catch (InterruptedException ie) {
                log.warn("Callback was interrupted!");
                ie.printStackTrace();
//              callbackQueue.put(ie); // plausible? or wrap in while loop?
//              TODO now external callbackQueue.get hangs forever !!
                return;
            }

//          required ? (in all of the above)
            super.onDone(exchange);
        }
    }

    /**
     * Currently this should be done explicitly for a graceful shutdown. It should
     * be embedded in a higher level context like org.apache.camel.main.Main being
     * a command line tool for booting up a CamelContext.
     */
    public void tearDown() throws Exception {
        executorService.shutdown();
//      check for pending messages in messageQueue .. wait?
        device.stop();
        context.stop();     // for ALL devices ?
    }

//  remove/improve by using DSL .delay(1000) in Camel Route
    private static void delay(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
//          Thread.sleep(millis);   // traditional 
        } catch (InterruptedException e) {
            System.err.println("Delay was interrupted: " + e.getMessage());
//          e.printStackTrace();
        }
    }

    // in case the decoder needs to be switched ..
    private byte[] decodeHexDump(String hexString) {
        return ByteBufUtil.decodeHexDump(hexString);
//      this is not part of java +8 anymore
// 2.   javax.xml.bind.DatatypeConverter.parseHexBinary(hexString);
// 3.   org.apache.commons.codec.binary.Hex
//      try {return Hex.decodeHex(string);}
//      catch (DecoderException e) {}
    }

//  -----------------------------------------------------------------

    /*  wrap tearDown in finalization ?
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("finalizing Device instance...");
    } */

    /** get {@link #reconnectTimeout}
    public int getReconnectTimeout() {
        return reconnectTimeout;
    } */

    /** set {@link #reconnectTimeout}
    public void setReconnectTimeout(int reconnectTimeout) {
        this.reconnectTimeout = reconnectTimeout;
    } */

    /**
     * Timeout in seconds to wait for server problems when sending a message via
     * request only.
    private long requestOnlyTimeout = 2;     */

    /* requestTimeout (producer)
     * 
     * Allows to use a timeout for the Netty producer when calling a remote server.
     * By default no timeout is in use. The value is in milli seconds, so eg 30000
     * is 30 seconds. The requestTimeout is using Netty’s ReadTimeoutHandler to
     * trigger the timeout.
     */

    /** get {@link #requestOnlyTimeout} 
    public long getRequestOnlyTimeout() {
        return requestOnlyTimeout;
    }   */

    /** set {@link #requestOnlyTimeout} 
    public void setRequestOnlyTimeout(long requestOnlyTimeout) {
        this.requestOnlyTimeout = requestOnlyTimeout;
    }   */

}
