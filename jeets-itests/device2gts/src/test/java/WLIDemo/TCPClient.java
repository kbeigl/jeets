package WLIDemo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

/**
 * Client for sending messages to TCPServer provided by WLI or to any other,
 * i.e. PTC Server. Currently the PTC server logs all HEX messages to
 * box_{IP}.log file where they can be picked up and sent one by one to debug
 * and trace the login procedure and much more ...
 * 
 * @author kbptc
 */
public class TCPClient {
	// stackoverflow.com/questions/25186924

	public static void main(String[] args) {
		TCPClient tracker = new TCPClient();
	    tracker.runClient();
	}

	public void runClient () {
		int port = 3001;      // TCPServer
		String host = "localhost";
//	    WirelessLink PTC Server 
//        int port = 20000;
//      ptc lenovo external (tmp) 87.184.102.62  internal 192.168.178.31
//      ptc janus-2 195.82.63.162
//        String host = "192.168.178.31";

//	    URL url = this.getClass().getClassLoader().getResource("box_213.162.73.247.log");
//		URL url = this.getClass().getResource("box_213.162.73.247.log");
//	    System.out.println(url.getPath());

        File hexFile = new File("src/test/resources/data/wli_191107-0930.log");
//        File hexFile = new File("src/test/resources/data/box_212.95.5.148.log");
//        File hexFile = new File("src/test/resources/data/box_46.125.250.17.log");
//        File hexFile = new File("src/test/resources/data/box_213.162.73.247.log");
	    
		String hex[] = loadHexMessagesFromFile(hexFile);
		
		if (hex == null) return;
		System.out.println( hex.length + " messages were read from file.");

		Socket socket = null;
		try {
			socket = new Socket(host, port);
			for (int i = 0; i < hex.length; i++) {
				byte[] request = null;
				try {
					request = DatatypeConverter.parseHexBinary(hex[i]);
				} catch (IllegalArgumentException e) {
					System.err.println("HEX string " + hex[i] + "\ndoes not conform to lexical value space:\n" + e.getMessage());
				}
				System.out.println("writeMessage " + hex[i]);
				writeMessage(socket, request);
				try {
//					retry after x seconds - see jeets-tracker
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					System.err.println("Client interrupted: " + e.getMessage());
				}
			}

//			byte [] message = readMessage(socket);
//			System.out.println("read message " + Arrays.toString(message));

//			wait a bit before closing connection
			try { Thread.sleep(10000); } 
			catch (InterruptedException e) {}
//			move to try .. catch .. finally {
			socket.close();
		} catch (IOException e) {
			System.err.println("Client IO fault: " + e.getMessage());
			System.exit(-1);
		}
	}

	/**
	 * Currently the PTC WirelessLinks server is logging all messages in hex line by
	 * line for all connections of one box via one IP, i.e. box_212.95.5.148.log
	 * These messages can be picked up and send with this client for testing,
	 * debugging, parsing messages etc.
	 */
	private String[] loadHexMessagesFromFile(File hexFile) {
		List<String> list = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(hexFile));
		} catch (FileNotFoundException e) {
			System.err.println("'" + hexFile + "' could not be found");
			return null;
		}
		try {
			String message = "init";
			while (message != null) {
				message = reader.readLine();
				if (message != null) list.add(message);
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("reading '" + hexFile + "' was interrupted due to IO problems");
			return null;
		}
		return list.toArray(new String[0]);
	}

	public void writeMessage (Socket socket, byte [] myByteMessage) {
		
		/* stackoverflow.com/questions/4860590 is more straight forward to read and write
		 * yet this works for testing
		 * most importantly it flushes the complete byte[] message at once
		 * opposed to the TCPServer that sends each byte via loop.
		 */

	    try {
	        OutputStream out = socket.getOutputStream();
	        DataOutputStream dos = new DataOutputStream(out);
	        dos.write(myByteMessage, 0, myByteMessage.length);
			PrintWriter printWriter = 
					new PrintWriter(
							new OutputStreamWriter(socket.getOutputStream()));
	        printWriter.print(myByteMessage);
	        printWriter.flush();
	    }
	    catch (Exception e) {
	        System.out.println("Could not send data over TCP");
	        return;
	    }
	}

	public byte[] readMessage (Socket socket) {

		/* This method is experimental and blocking until a byte is read.
		 * The InputStream should be read via loop byte by byte and check
		 * WLI start / stop to excite it - like a frame decoder.
		 * Also the reader should run in a separate thread, since WLI 
		 * does not (dis)acknowledge messages, but can send asynchroneous
		 * messages any time, i.e. GPSrequest in TCPServer.
		 */
		int msgLength = 15;
	    try {
	        byte[] buf = new byte[msgLength];
//	        full message should be on the wire, i.e. socket connection
	        int len = socket.getInputStream().read(buf, 0, msgLength);

	        System.out.println(Arrays.toString(buf));
//	        [2, 49, 0, 0, 0, 6, -2, 0, -55, 0, 1, 0, 52, 0]
	        
	        return buf;
//	        read() only returns -1 if endofstream -> server closes socket
//	        while((len = socket.getInputStream().read(buf))!=-1){
	        
//	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//	            baos.write(buf, 0, len);
//	        }
//	        for (int i = 0; i < baos.toByteArray().length; i++) {
//	            System.out.println(baos.toByteArray()[i]);
//	        }
//	        return baos.toByteArray();
	    }
	    catch (Exception e) {
	        System.out.println("Server fault: "+ e.getMessage());
	    }
	    
//	    stackoverflow.com/questions/1176135
//	    also check .readFully
//	    DataInputStream dIn = new DataInputStream(socket.getInputStream());
//	    int length = dIn.readInt();                    // read length of incoming message
//	    if(length>0) {
//	        byte[] message = new byte[length];
//	        dIn.readFully(message, 0, message.length); // read the message
//	    }

	    return null;
	}

}