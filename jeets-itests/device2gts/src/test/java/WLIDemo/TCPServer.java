/**
  * WLI TCP Interface - Pure JAVA sample code
  * 
  * 	This sample code demonstrates:
  * 
  *     - how to parse incoming WLI TCP/IP packets and WLI Application messages received by WLI terminals.
  *     - how to initialize the TCP/IP socket waiting for incoming connections from the units
  *     - how to handle incoming connections and receive messages concurrently from multiple units.
  *     - how to replace escaped characters in a message with the original ones.
  *     - how to detect a unit connection reset, creating a new connection, and killing the old one
  *     - how to detect duplicate messages
  *     - how to send a message to the mobile unit
  *     - how to calculate application message checksum value.
  *     
  *     General functional description of this demo:
  *     
  *     + When this demo first started, the <main> function in the <TCPServer> class is called.
  *       The <main> function opens a port (default #3001) and listens for client connections.
  *       Once a client connects, the function spawns the client connection to a new thread
  *       so that the connection will be handled concurrently while the <main> function is waiting for a 
  *       other clients connection.
  *       The new client handling thread is created by creating a new instance of the class 
  *       TCPUnitConnection and "starting" it (which results with calling the <run> function of
  *       the TCPUnitConnection thread class).
  *     + Once the <run> function of the TCPUnitConnection thread is started, it opens a buffered 
  *       input stream to read messages from the connected unit.
  *       Note that it is preferable to use Buffered input stream and not a raw stream, as it requires 
  *       much less resources from the server (much less IO operations).
  *       The <run> function reads from the input stream until it gets the start of WLI packet char (STX), 
  *       than it reads the packet into a buffer until it receives the end WLI packet char (ETX).
  *       Note that while reading the message, the <run> function needs also to replace "escaped characters"
  *       with the original characters (for details see section 6.2 in the TCP/IP interface guide).
  *       Once a message is read, the <run> function calls the <parseMessage> function.
  *     + The <parseMessage> checks the first character of the message, which designates the class of the
  *       message, and calls a function to parse the specific message class.
  *       Note that first message must be the presentation message, if the <parseMessage> detects a message
  *       from an unidentified unit, it will raise an exception (i.e. that unit connection will be aborted).
  *       After a successful connection message, the <parseMessage> add the client connection to an hash table
  *       by calling the <addToHash> function, which checks if an old connection exists to the same unit, and 
  *       if that so, the <addToHash> function terminates that old connection thread (and closes the old connection).
  *       After handling the presentation message, most of the messages are application messages, that are 
  *       Handled by the <parseAppMsg> function.
  *     + The parseAppMsg is a huge switch that handles all the possible application messages (note that
  *       the demo implements the break down ONLY of the GPS message).
  *       To enable to manipulate easily the different application messages, the <parseAppMsg> calls the 
  *       <parseAppParams> function which creates List of objects, each message parameter is encapsulated
  *       in one object. There are 2 object types (classes) one for Textual fields and the other for
  *       Binary fields.
  *       
  *     + To send a GPS request from the program:
  *       Press the letter x<and press enter>
  *       At server startup, The <main> function in the <TCPServer> class spawns a keyboard listener
  *       class <UserCommands> thread that waits for the user to click X<enter> on the keyboard.
  *       Every time the X is entered, the listener loops on all connected units instances (that are kept 
  *       in an hash table), and calls their <sendGPSRequest> method.
  *     
  *       
  * A sample run of the program generated the following output once it got a message:
  * 
  * Message: <0x32><0x77><0x6c><0x69><0x3a><0x30><0x32><0x39><0x32><0x34><0x36><0x32><0x35><0x31><0x31><0x36><0x30> :END
  * Presentation message, WLI Unit: 029246251160
  * 
  * Message: <0x31><0x23><0x3e><0x0><0x82><0xaa><0x2d><0xc9><0x0><0x1><0x0><0x34><0x0><0x5><0x0><0xff><0x0><0x10><0x1><0x25><0xfa><0x4a><0x1><0x3e><0xfe><0x49><0x0><0xa><0xd3><0x1e><0xac><0x21><0xb><0xf4><0x0><0x6><0x0><0x36><0x0><0x9><0x0><0x32><0x38><0x34><0x32><0x32><0x37><0x0><0x13><0x0><0x39><0x30><0x30><0x35><0x0><0x14><0x0><0x30><0x2c><0x30><0x2c><0x30><0x2c><0x30><0x0><0xf6><0x0><0x31><0x2c><0x34><0x2c><0x31><0x33><0x37><0x38><0x2c><0x34><0x32><0x31><0x0><0xf7><0x0><0x38><0x30><0x35><0x38><0x38><0x37><0x0><0xf8><0x0><0x33><0x30><0x2c><0x30><0x2c><0x30><0x2c><0x30><0x2c><0x30><0x0><0xf9><0x0><0x31><0x0><0xfa><0x0><0x31><0x30><0x30><0x0><0xfb><0x0><0x31><0x39><0x32><0x0><0xfc><0x0><0x31><0x39><0x32><0x0><0xff><0x0><0x31><0x32><0x39><0x33><0x33><0x36><0x39><0x35><0x31><0x32><0x0> :END
  * GPS Msg #9022, length: 130, checksum: 43565
  *      GPS Format: Short Extended
  *      Short GPS: Latitude(deg):32.110203, Longitude(deg):34.842575, Speed (knots): 1.0, Time: 12/26/2010 13:18:33, Course (deg): 306.0
  *      GPS Sent reason: Time interval #1 expiration
  *      GPS Distance (feet): 284227
  *      GPS message number: 9005    
  *      Acceleration alarm counters: Sharp turns:0, Quick accelerations:0, Sudden brakes:0, Sharp lanes crossing:0
  *      A2D Input (volts): 0.05
  *      Engine switch state: 0
  *      Total number of GPS satellites in view: 10
  *      Signal-to-Noise Ratio array: 41,24,40,42,22,28,15
  *      Number of satellites used for position calculation: 7
  *      Current HDOP: 1.2
  *      Cell - Mobile Country Code: 425
  *      Cell - Mobile Network Code: 01
  *      Cell - Location Area Code: 61100
  *      Cell - cell ID: 31073
  *      Cell - Received Signal Strength: 27 
  *      Extended power: Unit Type: Piccolo Plus with internal battery, Power State: External power (ACIN), Ext Power Voltage:11.64, Battery voltage:4.21
  *      Unit serial number: 805887
  *      Bat conv rule: 1
  *      Unit internal temperature (Fahrenheit): 100.0
  *      Battery Level on unit start (Volts): 13.714286
  *      Current Battery Level (Volts): 13.714286
  *      Message Time: Sun Dec 26 15:18:32 IST 2010
  * 
 */
package WLIDemo;

import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.text.*;

/**
 * Main class of the program.
 * <p>
 * The <main> function opens a port (default #3001) and listens for client
 * connections. Once a client connects, the function spawns the client
 * connection to a new thread so that the connection will be handled
 * concurrently while the <main> function is waiting for other clients
 * connection.
 */
public class TCPServer {
    static final int LISTENING_PORT = 3001;

    public static void main(String argv[]) throws Exception {
        System.out.print("Server started ");

        // Start the keyboard listener
        new UserCommands().start();
        System.out.println("click X<enter> to send GPS requests to all connected units");

        ServerSocket serverSocket = new ServerSocket(LISTENING_PORT);
        while (true) {
            // Wait for a new unit connection
            Socket connectionSocket = serverSocket.accept();
            if (connectionSocket != null) {
                // Open a new thread which will handle the new unit connection
                TCPClientConnection terminalConnect = new TCPClientConnection();
                terminalConnect.connectionSocket = connectionSocket;
                terminalConnect.start();
            }
            Thread.sleep(1000); // give the CPU a break
        }
    }
}

/**
 * Keyboard listener thread that waits for the user to click X<enter> on the
 * keyboard. Every time the X is entered, the listener loops on all connected
 * units instances (that are kept in an hash table), and calls their
 * <sendGPSRequest> method.
 */
class UserCommands extends Thread {
    public void run() {
        while (true) {
            try {
                // Read a char from the console input.
                // Note that in Java the input is buffered which means
                // that the app will get it only when the user clicks the <enter> key.
                char c = (char) System.in.read();
                switch (c) {
                case 'x':
                case 'X':
                    // Get the active connections hash table
                    Hashtable<String, TCPClientConnection> unitsHashtable = TCPClientConnection.unitsHashtable;
                    Enumeration<TCPClientConnection> elements = unitsHashtable.elements();
                    // Check that we have at least one connected unit
                    if (!elements.hasMoreElements()) {
                        System.out.println("No unit is connected...");
                        continue;
                    }
                    // Loop on all connected units and send a GPS request to them.
                    while (elements.hasMoreElements()) {
                        TCPClientConnection unitConnection = elements.nextElement();
                        System.out.println("Sending GPS request nessage to unit: " + unitConnection.unitID);
                        unitConnection.sendGPSRequest();
                    }
                    break;
                default:
                    // do nothing...
                }
            } catch (IOException e) {
                // Do nothing...
            }
        }
    }
}

/**
 * class AppParam for derived classes AppParamText and AppParamBinary.
 */
abstract class AppParam {
	int  paramNumber;
	abstract public String strValue();
	abstract public int[] binValue();
}

/**
 * class AppParamText
 * When WLI application message is parsed, An instance of
 * this class is created for each of the a TEXT parameters.
 */
class AppParamText extends  AppParam {
	String value;
	public String strValue() {
		return value;
	}
	public int[] binValue() {
		return null;
	}
}

/**
 * class AppParamBinary
 * When WLI application message is parsed, An instance of this class 
 * is created for each of the a Binary parameters.
 * Note that the size part of the parameter is not stored in the instance
 * as you can get it with the length attribute, e.g. param.binValue().length
 */
class AppParamBinary extends  AppParam {
	int value[];
	public String strValue() {
		return "Error";
	}
	public int[] binValue() {
		return value;
	}
}

class TCPClientConnection extends Thread {

	static final int MAX_WLI_PCK_SIZE   = 1500;

	static final int STX_START_WLI_PCK  = 0x02;
	static final int ETX_END_WLI_PCK    = 0x03;
		
	static final int ESCAPE_HDR = 0xDB;
	static final int ESCAPE_STX = 0xD2;
	static final int ESCAPE_ETX = 0xD3;
	static final int ESCAPE_DB  = 0xDD;
	
	// Classes
	static final int APP_CLASS          = 0x31;
	static final int PRESENTATION_CLASS = 0x32;
	static final int KEEP_ALIVE_CLASS   = 0x34;

	// APP MSG Types
	static final int APP_GPS_MSG     = 0xC9;
	static final int APP_NO_GPS_MSG  = 0xE4;
	static final int APP_LOC_BOUND   = 0xCD;
	static final int APP_IO_STATE    = 0xCB;
	static final int APP_LOGIN       = 0xFE;
	static final int APP_LOGOUT      = 0xFD;
	static final int APP_LOW_BAT     = 0xFB;
	static final int APP_EMERGENCY   = 0x0a;
	static final int APP_IN_COVERAGE = 0xF8;
	static final int APP_ACK         = 0xFC;
	static final int APP_IP_CHANGED  = 0x17;
	
	// GPS FIELD NUMBERS
	static final int FLD_GPS_FORMAT       = 1;
	static final int FLD_GPS_LONG 	      = 2;
	static final int FLD_GPS_SHORT        = 5;
	static final int FLD_GPS_REASON       = 6;
	static final int FLD_GPS_STOP_LEN     = 7;
	static final int FLD_GPS_STOP_TIME    = 8;
	static final int FLD_GPS_DISTANCE     = 9;
	static final int FLD_GPS_START_STATE  = 10;
	static final int FLD_GPS_MSG_NUM      = 19;
	static final int FLD_GPS_ACC_ALRM_CNT = 20;
	static final int FLD_GPS_A2D          = 22;
	static final int FLD_GPS_ENGINE_STATE = 26;
	static final int FLD_GPS_NUM_SAT      = 27;
	static final int FLD_GPS_SV_SNR       = 28;
	static final int FLD_GPS_SAT_USED     = 29;
	static final int FLD_GPS_HDOP         = 30;
	static final int FLD_GPS_CELL_MCC     = 35;
	static final int FLD_GPS_CELL_MNC     = 36;
	static final int FLD_GPS_CELL_LAC     = 37;
	static final int FLD_GPS_CELL_ID      = 38;
	static final int FLD_GPS_GSM_RSSI     = 39;
	static final int FLD_GPS_CELL_RAT     = 42;
	static final int FLD_GPS_CELL_RAC     = 43;
	static final int FLD_GPS_ALARMLINK_STATE  = 45;
	static final int FLD_GPS_IOS_STATE     = 46;
	static final int FLD_GPS_IN1_COUNTER   = 47;
	static final int FLD_GPS_START_STATE_EX  = 50;
	static final int FLD_GPS_NEW_BIN_FORMAT_READINGS = 51;
	static final int FLD_GPS_SHORT_NEW_FORMAT_1ST  = 52;
	static final int FLD_GPS_SHORT_NEW_FORMAT_LAST  = 66;

	// COMMON FIELD NUMBERS
	static final int FLD_GLB_PWR          = 246;
	static final int FLD_GLB_SERIAL_NUM   = 247;
	static final int FLD_GLB_BAT_CONV     = 249;
	static final int FLD_GLB_INTERN_TEMP  = 250;
	static final int FLD_GLB_BAT_AT_STRT  = 251;
	static final int FLD_GLB_BAT_NOW      = 252;
	static final int FLD_GLB_TIME         = 255;

	// Reserved Fields to be ignored
	static final int FLD_GLB_RESERVED_248 = 248;
	static final int FLD_GLB_RESERVED_40  = 40;

	static String strGPSReason[] = {
		"Response to GPS request", 
		"GPS service start",
		"Entry to �No Move� state",
		"�No move� timeout expired",
		"Vehicle started to move",
		"Time interval #1 expiration",
		"Time interval #2 expiration",
		"Time interval #3 expiration",
		"Preconfigured distance #1 passed",
		"Preconfigured distance #2 passed",
		"Preconfigured distance #3 passed",
		"Entry to Boundaries region",
		"Exit from Boundaries region",
		"Speeding detected",
		"Heading change"
		};

	static String externalPowerType[] = {
		"Piccolo Plus without internal battery", 
		"Piccolo Plus with internal battery",
		"Piccolo AT with alkaline of NiMh battery",
		"Piccolo AT with lithium battery"
		};
 
	public Socket connectionSocket = null;
	public String unitID = null;
	
	// To detect message duplication,
	// A message is considered duplicated, when its sequence number 
	// equals the sequence number of a previous message.
	private int lastMessageID = -1;
	
	static void print(String str) {
		System.out.print( str);
	}
	static void println(String str) {
		System.out.println( getCurrentTimeStamp() + ": " + str);
	}

	// Message Sequence Number used to prevent duplicated messages
	// In an actual non demo code, you may want to keep that value
	// persistent.
	int hostToUnitMsgSeqNumber = 0;
	
	public static String getCurrentTimeStamp() {
	    SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss.SSS");
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    return strDate;
	}

	/**
     * function  sendApplicationMessage
     * Generic function to send WLI Application messages
	 * 
	 * @param bufMessage The specific application message data
	 * @param msgSize The specific application message data size
	 */
	public void sendApplicationMessage(int bufMessage[], int msgSize) {

		int[] header = new int[6];

		// Create Application message packet class header
		
		// Message Sequence Number: always use value 0 (Piccolo Terminal does not check it)
		hostToUnitMsgSeqNumber = 0;
		header[0]  = ((hostToUnitMsgSeqNumber>>8) & 0xFF);
		header[1]  = hostToUnitMsgSeqNumber & 0xFF;

		// Message length
		header[2]  = ((msgSize>>8) & 0xFF);
		header[3]  = msgSize & 0xFF;

		// Application message checksum
		int checksum = calculateWLICheckSum(bufMessage, 0, msgSize-1);
		header[4]  = (checksum>>8) & 0xFF;
		header[5]  = checksum & 0xFF;

		int buf[] = new int[MAX_WLI_PCK_SIZE];
		int idx = 0;

		buf[idx++]  = STX_START_WLI_PCK;
		buf[idx++]  = APP_CLASS;

		// Escape characters in the header (6 bytes) and add to buffer
		for(int j=0; j<6; j++) {
			switch(header[j]) {
				case STX_START_WLI_PCK:
					buf[idx++] = ESCAPE_HDR;
					buf[idx++] = ESCAPE_STX;
					break;
				case ETX_END_WLI_PCK:
					buf[idx++] = ESCAPE_HDR;
					buf[idx++] = ESCAPE_ETX;
					break;
				case ESCAPE_HDR:
					buf[idx++] = ESCAPE_HDR;
					buf[idx++] = ESCAPE_DB;
					break;
				default: 
					buf[idx++] =  header[j];
					break;
			}
		}
        
		// Escape characters in the data
		for(int i=0; i<msgSize; i++) {
			switch(bufMessage[i]) {
				case STX_START_WLI_PCK:
					buf[idx++] = ESCAPE_HDR;
					buf[idx++] = ESCAPE_STX;
					break;
				case ETX_END_WLI_PCK:
					buf[idx++] = ESCAPE_HDR;
					buf[idx++] = ESCAPE_ETX;
					break;
				case ESCAPE_HDR:
					buf[idx++] = ESCAPE_HDR;
					buf[idx++] = ESCAPE_DB;
					break;
				default: 
					buf[idx++] =  bufMessage[i];
					break;
			}
		}
		buf[idx] = ETX_END_WLI_PCK;
		// Result for GPS request: 
		// buf[] = 0x02 0x31 0x00 0x00 0x00 0x06 0xFE 0x00 0xC9 0x00 0x01 0x00 0x34 0x00 0x03 (14 bytes) 
					
		// Send the message byte by byte (not writeln)
		try {
			OutputStream out = connectionSocket.getOutputStream();
			for(int i=0; i<idx+1; i++) {
				out.write((byte)buf[i]);
			}
		} catch (IOException e) {
			System.out.println("Failed to send GPS request: " + e.getMessage());			
		}	
	}

	/**
	 * function  calculateWLICheckSum
	 * Send each APP message contains a checksum in the header
	 * This function is used to calculate the checksum value.
	 */
	public int calculateWLICheckSum(int buffer[], int startIdx, int endIdx)
	{
		int	  loopCounter;
		long  total = 0;
		int	  addVal;
		int msgSize = endIdx - startIdx + 1;
		
		// Add each couple of bytes as short number (network order).
		for(loopCounter=startIdx; loopCounter<=endIdx; loopCounter+=2){
			total += (buffer[loopCounter] << 8) + buffer[loopCounter+1];
		}
		// If we have odd bytes - add the last one
		if(msgSize%2 != 0){
			total += buffer[msgSize - 1] << 8;
		}
		
		// Make sure the result is not bigger than short (2 bytes)
		while (total > 0xFFFF){
			addVal = (int)((total >> 16) & 0xffff);
			total = (long)(total & 0xFFFF) + addVal;
		}
		return (int)total;
	}

	static final int GPS_REQUEST_MSG_LEN = 6;
	/**
	 * function  sendGPSRequest
	 * Send GPS request to the connected unit.
	 */
	public void sendGPSRequest() {
		
		int buf[] = new int[GPS_REQUEST_MSG_LEN];
		
		// Start of the body of GPS Request Message
		// Message type
		buf[0]   = APP_GPS_MSG;
		// Field 1: GPS data format flag 
		// Filed #
		buf[1]  = 0x00;
		buf[2] = 0x01;
		buf[3] = 0x00;
		buf[4] = '4';  // (4 = Short High Precision Extended)
		buf[5] = 0x00; // End of field #1
		
		sendApplicationMessage(buf, GPS_REQUEST_MSG_LEN);
	}

	static Hashtable<String, TCPClientConnection> unitsHashtable = new Hashtable<String, TCPClientConnection>();

	/**
	 * Add a connection to the connection hash table
	 * <p>
	 * If a connection to the same unit already exist, the old connection will be first removed.
	 * This function is static (as the hash table is common to all threads), this function is the
	 * only one that modifies the hash table.
	 * To make the function thread safe, it is set as <synchronized>
	 */
	static synchronized void addToHash(String unitID, TCPClientConnection connection) {
		// First check if old connection exists to this unit- if so, drop it...
		TCPClientConnection prevConnection = unitsHashtable.get(unitID);
        if(prevConnection != null) {
        	unitsHashtable.remove(unitID);
        	try {
        		prevConnection.connectionSocket.close();
        		prevConnection.connectionSocket = null;
			} catch (IOException e) {
				println("Close socket exception.");
				// Do nothing...
			}
        	// Interrupt the old thread - so it will exit...
			prevConnection.interrupt();
        } 
        unitsHashtable.put(unitID, connection);
	}
	
	/**
	 * function  parsePresentationMsg
	 * The Presentation Msg is sent by the unit to the server on the server connection
	 * It is a very simple message that contains the Unit ID
	 */
	private void parsePresentationMsg(int message[], int messageSize){
//      WirelessLinks presentation message
//      02  32       77 6c 69 3a 30 31 32 31 32 33 31 32 33 30 30 31 03
//      STX Pres.Msg  w  l  i  :  0  1  2  1  4  3  1  2  3  0  0  1 ETX
//	    String msgHex ="0232776c693a30313231323331323330303103";
		if(messageSize <  17) {
			println("Illegal Presentation Class, size is smaller than minimum.");
			return;
		}
		// Check that the message starts with "wli:"
		if((message[1] != (int)'w') || (message[2] != (int)'l') || (message[3] != (int)'i') || (message[4] != (int)':')) {
			println("Illegal Presentation Class, does not start with 'wli:'");
			return;
		}
		// Print the unit ID
		print("Presentation message, WLI Unit: ");
		unitID = "";
		for(int i=0; i<12; i++){
			unitID += (char)message[5+i];
		}
		println(unitID);
	}	

	/**
	 * function  parseAppParams
	 * This function gets raw bytes array that contains the Application
	 * message parameters, parse it, and creates a Parameter object for each
	 * parameter in the message.
	 * There are 2 possible classes of Parameter objects: AppParamText and AppParamBinary 
	 */
	private ArrayList<AppParam> parseAppParams(int message[], int messageSize){
		int currIdx = 9;
		int fieldLen;
		ArrayList<AppParam> paramArray = new ArrayList<AppParam>();
		// loop on all the message params bytes
		while(currIdx < (messageSize -1)) {
			// First byte is the field number
			int fieldNum = message[currIdx++];
			// Sanity check, a zero must follow the field number
			if(message[currIdx++] != 0) {
				println("     Error, Field #" + fieldNum + " not followed by 0");
				return null;
			}
			// If param value starts with 0xFF - its a Binary field
			if(message[currIdx] == 0xff) {
				// This is a Binary parameter
				AppParamBinary param = new AppParamBinary();
				param.paramNumber = fieldNum;
				currIdx++;
				// Next 2 bytes are the binary data length
				fieldLen = message[currIdx]*256 + message[currIdx+1];
				currIdx += 2;
				// Allocate buffer, and copy the parameter binary data into it.
				param.value = new int[fieldLen];
				int fieldEnd = currIdx+fieldLen;
				for(int i=0;currIdx < fieldEnd; currIdx++, i++) {
					param.value[i] = message[currIdx];
				}
				paramArray.add(param);
				currIdx++;
			} else {
				// This is a text parameter
				AppParamText param = new AppParamText();
				param.paramNumber = fieldNum;
				// Textual field, just copy the text (stop when you reach to the end of the 
				// field - marked with byte with value 0
				param.value = "";
				while(message[currIdx] != 0x00) {
					param.value += (char)message[currIdx];
					currIdx++;
				}
				paramArray.add(param);
				currIdx++;
			}
			
		}
		return paramArray;
	}

	/**
	 * function  strExtendedPowerState
	 * A simple function that returns the string representation 
	 * of a specific power state value 
	 */
	private String strExtendedPowerState(int powerState) {
		switch(powerState) {
		case 0:
			return "No external power";
		case 4:
			return "External power (ACIN)";
		case 8:
			return "Power by USB";
		case 12:
			return "External power (ACIN) and by USB";
		default:
			return "Unknown Extended Power State: " + powerState;
		}
	}
		
	/**
	 * function  converBatValue
	 * A simple function that computes the battery level as a  
	 * function of the value in the Battery Conversion rule (field #249) 
	 */
	private String converBatValue(int batConversion, int rawVal) {
		switch(batConversion) {
		case -1:
			return "Bat conversion was not set";
		case 0:
			return (rawVal == 255)?"External Power":"Low";
		case 1:
			return "" + (float)rawVal/14;
		case 2:
			return "" + ((float)rawVal/14 + (float)0.7);
		case 3:
			return "" + ((float)rawVal)/51;
		default:
			return "Unknown Battery conversion: " + batConversion;			
		}
	}
	
	/**
	 * function  printGPSParams
	 * Print out the textual rendering of the received GPS message 
	 * Note that this is a huge switch, as its purpose is to show access
	 * to all the possible GPS message parameters. 
	 * In most cases you just need a small portion of the data in the message
	 * and thus, you can implement only the handling of the parameters that
	 * you need, and just ignore the others.
	 *
	 */
	private void printGPSParams(ArrayList<AppParam> params){
		Iterator<AppParam> it=params.iterator();
		int batConversion = -1;
		int AtModeStartState = -1;
        while(it.hasNext())
		{
        	AppParam param =(AppParam)it.next();
        	switch(param.paramNumber) {
        	case FLD_GPS_START_STATE:
        		// This field always present when the unit is ruiing in AT mode
        		AtModeStartState = Integer.parseInt(param.strValue());
         		print("     AT mode start state  " + AtModeStartState + " ");	
        		switch(AtModeStartState)
        		{
        		case 0:
        			print("(no vibrations, if sent, check field 50 for details)");
        			break;
        		case 1:
        			print("(vibrations continue)");
        			break;
        		case 2:
        			print("(wakeup by vibration)");
        			break;
        		default:
        			print("(unknown)");
        			break;
        		}
         		println("");	
        		break;
        	case FLD_GPS_START_STATE_EX:
        		if (AtModeStartState == 0)
        		{
        			 // no vibrations state 
        			// This field is sent starting from Piccolo FW 2.96 (May 2013) 
        			int AtModeStartStateEx =  Integer.parseInt(param.strValue());
             		print("     AT mode start state extention " + AtModeStartStateEx + ":");	
        			if (AtModeStartStateEx == 0)
            			print("no vibrations continue");
        			else
            			print("vibrations end");
             		println("");	
        		}
        		break;
        	case FLD_GPS_FORMAT:
        		println("     GPS Format: "+(param.strValue().equals("1")?"Long":(param.strValue().equals("4")?"Short Extended":"Unknown")));
        		break;
        	case FLD_GPS_SHORT:
        		print("     Short GPS: ");
        		int [] binVal = param.binValue();
        		// Sanity check - GPS EXT msg must be 16 bytes long
        		if(binVal.length != 16) {
            		println(" - length incorrect: "+binVal.length);
            		break;
        		}
           		// latitude/longitude, The value represents 1/10000 of minutes, values greater than 0 represent 
           		// the North(latitude)/West(longitude) hemisphere, and values less than 0 represent the South(latitude)/West(longitude) hemisphere. 
           		// To convert to degrees, divide the value by 600000
        		float latitude = ((float)((binVal[0]<<24) + (binVal[1]<<16) + (binVal[2]<<8) + binVal[3]))/600000;
        		float longitude = ((float)((binVal[4]<<24) + (binVal[5]<<16) + (binVal[6]<<8) + binVal[7]))/600000;
        		print("Latitude(deg):" + latitude + ", Longitude(deg):" + longitude);
        		
        		// Speed over Ground, the value is 0.1 knots, so divide by 10 to get it by knots
        		// To convert to MPH: multiply by 1.151
        		// To convert to KM/H: multiply by 1.852
        		float speed = ((binVal[8]<<8) + binVal[9])/10;
           		print(", Speed (knots): " + speed);
           		
        		// Get date & time of GPS message
           		int minutsInDay = ((binVal[10] & 0x7)<<8) + binVal[11];
        		int minutes = minutsInDay%60;
        		int hours = minutsInDay/60;
        		int dayOfMonth = ((binVal[10] & 0xF8)>>3);
        		int month = (binVal[12] & 0x0F);
        		int year = 2000 + ((binVal[12] & 0xF0)>>4);

        		// ***** Year bug workaround (Alex: May 2013 ) ********

                // In short high precision extended format (4) year is sent using 4 bits (0...15).
                // We suppose that the century is 20 always
                // As a result in 2016,2032 etc the Year sending value will be 0 + 2000 = 2000, in 2017 - 1 + 2000 = 2001 etc. 
                // The workaround is based on the following: 
                        //  when the WLI unit is running and communcates with host,the UTC year received from GPS may be:
                        //  same as host computer year;
                        //  one year after then the computer UTC year ( e.g., due to computer clock delay at Dec 31 - Jan 01)
                        //  one year before then the computer UTC year ( e.g., due to GPS sending  delay)
                 // The workaround algorithm implements the assertion that GPS UTC time can not be more then 14 years before
                  // current UTC time as set on the computer
        		
        		// get current year of the computer clock
    		    Date date = new Date();
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                cal.setTime(date);
                int CurrentComputerYear = cal.get(Calendar.YEAR);
                // update year received from GPS (not older then 14 years(
       		    int DeltaYear = CurrentComputerYear - year;
       		    while (DeltaYear > 14)
    		    {
	       			year += 16;
	    			DeltaYear -= 16;
	    		}
        		// ***** Year bug workaround end (Alex: May 2013 ) ********

        		print(", Time: " + month + "/" + dayOfMonth + "/" + year + " " + hours + ":"+ minutes + ":" + binVal[13]);
        		
        		// Course is tenth of degrees, so divided by 10 to get it in degrees
        		float course = ((float)(binVal[14]<<8) + binVal[15])/10;
        		print(", Course (deg): " + course);
               	
        		println("");
        		break;        		

        	case FLD_GPS_NEW_BIN_FORMAT_READINGS:
        		int readings = Integer.parseInt(param.strValue());
         		println("     New GPS format readings: " + readings);	
        		break;
        	case FLD_GPS_SHORT_NEW_FORMAT_1ST:
        	case FLD_GPS_SHORT_NEW_FORMAT_1ST+1:
        	case FLD_GPS_SHORT_NEW_FORMAT_1ST+2:
        	case FLD_GPS_SHORT_NEW_FORMAT_1ST+3:
        	case FLD_GPS_SHORT_NEW_FORMAT_1ST+4:
        	case FLD_GPS_SHORT_NEW_FORMAT_1ST+5:
        	case FLD_GPS_SHORT_NEW_FORMAT_1ST+6:
        	case FLD_GPS_SHORT_NEW_FORMAT_1ST+7:
        	case FLD_GPS_SHORT_NEW_FORMAT_1ST+8:
        	case FLD_GPS_SHORT_NEW_FORMAT_1ST+9:
        	case FLD_GPS_SHORT_NEW_FORMAT_1ST+10:
        	case FLD_GPS_SHORT_NEW_FORMAT_1ST+11:
        	case FLD_GPS_SHORT_NEW_FORMAT_1ST+12:
        	case FLD_GPS_SHORT_NEW_FORMAT_1ST+13:
        	case FLD_GPS_SHORT_NEW_FORMAT_LAST:
         		println("     GPS New bin format data # " + (param.paramNumber-FLD_GPS_SHORT_NEW_FORMAT_1ST + 1));
        		binVal = param.binValue();
        		// Sanity check - GPS EXT msg must be 16 bytes long
        		if(binVal.length < 24) {
            		println(" - length incorrect: "+binVal.length);
            		break;
        		}
        		
           		// latitude/longitude, The value represents 1/10000 of minutes, values greater than 0 represent 
           		// the North(latitude)/West(longitude) hemisphere, and values less than 0 represent the South(latitude)/West(longitude) hemisphere. 
           		// To convert to degrees, divide the value by 600000
        		latitude = ((float)((binVal[8]<<24) + (binVal[9]<<16) + (binVal[10]<<8) + binVal[11]))/600000;
        		longitude = ((float)((binVal[12]<<24) + (binVal[13]<<16) + (binVal[14]<<8) + binVal[15]))/600000;
        		print("         Latitude(deg):" + latitude + ", Longitude(deg):" + longitude);
        		
        		// Speed over Ground, the value is 0.1 knots, so divide by 10 to get it by knots
        		// To convert to MPH: multiply by 1.151
        		// To convert to KM/H: multiply by 1.852
        		speed = ((binVal[16]<<8) + binVal[17])/10;
           		print(", Speed (knots): " + speed);
        		// Course is tenth of degrees, so divided by 10 to get it in degrees
        		course = ((float)(binVal[18]<<8) + binVal[19])/10;
        		print(", Course (deg): " + course);

        		int distancefeet = (binVal[20]<<24) + (binVal[21]<<16) + (binVal[22]<<8) + binVal[23];
        		print(", Dist(ft): " + distancefeet);

        		// Get date & time of GPS message
        		year = binVal[1] * 100  + binVal[2];
        		month = binVal[3];
        		int DayOfMonth = binVal[4];
        		hours = binVal[5];
        		minutes = binVal[6];
        		int seconds = binVal[7];
                Calendar calNew = Calendar.getInstance(TimeZone.getDefault());
                // need month-1 because 0-based (January = 0)
                calNew.set(year, month+(Calendar.JANUARY-1),DayOfMonth,hours,minutes,seconds);
                Date dt = calNew.getTime();
                String s;
                Format formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:s");
                println("");
        		println("         UTC time: " + formatter.format(dt));
        		int reasonNew = binVal[0];
        		if((reasonNew > 0) && (reasonNew < strGPSReason.length)) {
               		println("         GPS Sent reason: " + strGPSReason[reasonNew-1]);	
        		}
        		else {
            		println("     GPS Sent unknown reason: " + reasonNew);
        		}
        		
               	
        		break;        



        	case FLD_GPS_REASON:
        		int reason = Integer.parseInt(param.strValue()) - 1;
        		if(reason > (strGPSReason.length - 1)) {
            		println("     GPS Sent unknown reason: " + param.strValue());
            		break;
        		}
        		println("     GPS Sent reason: " + strGPSReason[reason]);	
        		break;
        	case FLD_GPS_DISTANCE:
        		// The Total distance (in feet) since last GPS service start.
        		// Multiply by 0.3048 to get distance in meters
        		int distance = Integer.parseInt(param.strValue());
         		println("     GPS Distance (feet): " + distance);	
        		break;
        	case FLD_GPS_MSG_NUM:
        		int msgNum = Integer.parseInt(param.strValue());
         		println("     GPS message number: " + msgNum);	
        		break;
        	case FLD_GPS_ACC_ALRM_CNT:
        		String alarmCntArray[] = param.strValue().split(",");
        		if(alarmCntArray.length < 4) {
            		println("     GPS Acceleration alarm counters missing...");
            		break;	
        		}
         		println("     Acceleration alarm counters: Sharp turns:" + alarmCntArray[0] + 
         				", Quick accelerations:" + alarmCntArray[1] + 
         				", Sudden brakes:" + alarmCntArray[2] + ", Sharp lanes crossing:" + alarmCntArray[3]);	
        		break;
        	
        	case FLD_GPS_A2D:
        		// A2D Input data sending must be enabled in configuration
        		// To convert the A2D digital value to the original analog voltage value: A2D = V = A2D/40
          		println("     A2D Input (voults): " + ((float)Integer.parseInt(param.strValue()))/40);	
        		break;
        	case FLD_GPS_ENGINE_STATE:
          		println("     Engine switch state: " + param.strValue());	
        		break;
        	case FLD_GPS_NUM_SAT:
          		println("     Total number of GPS satellites in view: " + param.strValue());	
        		break;
        	case FLD_GPS_SV_SNR:
        		// Comma delimited array of SNR values (0�99 db) for tracked satellites as defined in NMEA. 
        		// The number of values in array may be less then Total number of satellites in view.
        		// The field is sent when at least one satellite is in view.
          		println("     Signal-to-Noise Ratio array: " + param.strValue());	
        		break;
        	case FLD_GPS_SAT_USED:
          		println("     Number of satellites used for position calculation: " + param.strValue());	
        		break;
        	case FLD_GPS_HDOP:
        		// Current HDOP (Horizontal Dilution of Precision), multiplied by 10
          		println("     Current HDOP: " + ((float)Integer.parseInt(param.strValue()))/10);	
        		break;
        		
            // Cellular Cell Info...
        	case FLD_GPS_CELL_MCC:
          		println("     Cell - Mobile Country Code: " + param.strValue());	
        		break;
        	case FLD_GPS_CELL_MNC:
          		println("     Cell - Mobile Network Code: " + param.strValue());	
        		break;
        	case FLD_GPS_CELL_LAC:
          		println("     Cell - Location Area Code: " + param.strValue());	
        		break;
        	case FLD_GPS_CELL_ID:
          		println("     Cell - cell ID: " + param.strValue());	
        		break;
        	case FLD_GPS_GSM_RSSI:
        		// Value 0: -113 dBm or less
        		// Value 1: -111 dBm
        		// Values 2..30: -109... -53 dBm
        		// Value 31: -51 dBm or greater
        		// Value 99: not known or not detectable (not sent, the field is empty)
          		println("     Cell - Received Signal Strength: " + param.strValue());	
        		break;    		
        	case FLD_GPS_CELL_RAT:
        		// Sent by Fw 2.95 and later
        		// 0 - GSM (2G)
        		// 2 - UTRAN (3G)
        		// 3 - GSM w/EGPRS (2.5G)
        		// 4 - UTRAN w/HSDPA (3G)
        		// 5 - UTRAN w/HSUPA (3G)
        		// 6 - UTRAN w/HSDPA and w/HSUPA (3G)
          		println("     Cell - RAT: " + param.strValue());	
        		break;
        	case FLD_GPS_CELL_RAC:
          		println("     Cell - rac: " + param.strValue());	
        		break;
        		     		
        	// Global Common Fields...
        	case FLD_GLB_PWR:
        		String powerArray[] = param.strValue().split(",");
        		if(powerArray.length < 4) {
            		println("     Extended power field values missing");
            		break;	
        		}
        		int unitType = Integer.parseInt(powerArray[0]);
        		if(unitType > 3) {
            		println("     Extended power unknown unit type: " + unitType);
            		break;	
        		}
        		int extPowerState = Integer.parseInt(powerArray[1]);

        		// EPV - external power voltage, in 0.01V, divide by 100 for Volts
        		float epv = ((float)Integer.parseInt(powerArray[2]))/100;
        		// BV - internal battery voltage, in 0.01V, divide by 100 for Volts
        		float bv = ((float)Integer.parseInt(powerArray[3]))/100;
         		println("     Extended power: Unit Type: " + externalPowerType[unitType] + 
         				", Power State: " + strExtendedPowerState(extPowerState) + 
         				", Ext Power Voltage:" + epv + ", Battery voltage:" + bv);	
        		break;
        	case FLD_GLB_SERIAL_NUM:
          		println("     Unit serial number: " + param.strValue());	
        		break;
        	case FLD_GLB_BAT_CONV:
          		println("     Bat conv rule: " + param.strValue());
          		batConversion = Integer.parseInt(param.strValue());
        		break;
        	case FLD_GLB_BAT_AT_STRT:
          		println("     Battery Level on unit start (Volts): " + converBatValue(batConversion, Integer.parseInt(param.strValue())));	
        		break;
        	case FLD_GLB_BAT_NOW:
          		println("     Current Battery Level (Volts): " + converBatValue(batConversion, Integer.parseInt(param.strValue())));	
        		break;
        	case FLD_GLB_INTERN_TEMP:
        		// Unit internal temperature
        		// Fahrenheit = 1.76 x Val - 76
        		// Celsius = 0.98 x Val - 60
        		float tempFahrenheit = ((float)Integer.parseInt(param.strValue()))*(float)1.76 - 76;
          		println("     Unit internal temperature (Fahrenheit): " + tempFahrenheit);	
        		break;
        	case FLD_GLB_TIME:
        		long time_t = Integer.parseInt(param.strValue());
        		Date msgDate = new Date(time_t*1000);
          		println("     Message Time: " + msgDate.toString());
        		break;
        	case FLD_GLB_RESERVED_40:
        	case FLD_GLB_RESERVED_248:
          		// Reserved - ignore;
        		break;   		
          default:
            	// We kept this demo short to make it clear. Add above case statements to 
            	// all other GPS fields you want to handle.
				println("     Field #" + param.paramNumber + ": " + param.strValue());				
       		break;
         	}
 		}
	}

	/**
	 * function  parseAppMsg
	 * A demo function that parse the unit application messages
	 * In this demo only the GPS message is handled, in actual application 
	 * you may want to implement also other application message types in a
	 * similar way.
	 */
	private void parseAppMsg(int message[], int messageSize){
		String outStr;
		if(messageSize <  8) {
			println("Illegal App Class, size is smaller than minimum.");
			return;
		}
		// First fields in every App message are message sequel number, message length, 
		// checksum, and message type.
		int seqNum = (message[1]<<8) + message[2];
		int length = (message[3]<<8) + message[4];
		int chksum = (message[5]<<8) + message[6];
		outStr = "Msg #" + seqNum + ", length: " + length + ", checksum: " + chksum;
		// Duplicated messages detection
		// A message is considered duplicated, when its sequence number equals the sequence
		// number of a previous message.
		if(seqNum == lastMessageID) {
			println("Duplicate message detected, ignoring. msg#:" + lastMessageID);
			return;			
		}
		lastMessageID = seqNum;
		int msgType = message[7];
		switch(msgType) {
			case APP_GPS_MSG:
				// We got GPS message - handle it here
				println("GPS " + outStr);
				ArrayList<AppParam> params = parseAppParams(message, messageSize);
				printGPSParams(params);
				break;
			case APP_NO_GPS_MSG:
				// The message is sent from the unit when it can not get a valid GPS Fix.
				println("NO GPS " + outStr);
				break;
			case APP_LOC_BOUND:
				// Location boundaries data
				// Sent by the unit as a response on the Location boundaries request
				println("Loc Bounderies " + outStr);
				break;
			case APP_IO_STATE:
				// IO States Report Message
				// Some of WLI terminals are equipped with built-in dry inputs and
				// dry outputs that may be used for different user purposes.
				// The unit sends this message in the following cases:
				//	� On the unit startup.
				//	� When one of the Input states is changed
				//	� On the input state request by the Host
				println("IO States " + outStr);
				break;				
			case APP_LOGIN:
				// This message is sent whenever the terminal is activated (powered on or reset).
				println("Login " + outStr);
				break;
			case APP_LOGOUT:
				// This message is sent whenever the terminal is turned off due to
				// configured Engine Off status
				println("Logout " + outStr);
				break;
			case APP_LOW_BAT:
				// The unit sends this message when the battery is low and 
				// should be immediately recharged.
				println("Low Battery " + outStr);
				break;
			case APP_EMERGENCY:
				// Some units has emergency button, and send the Emergency message 
				// when is button is pressed, or when the corresponding dry-contact is closed.
				// For Piccolo AT terminals this message is sent when the unit was waken up by vibration
				println("Emergency " + outStr);
				break;
			case APP_IN_COVERAGE:
				// The unit sends the message if the unit was disconnected from the network (for at
				// least 9 minutes of disconnection), and then connected again (the message is sent 
				// after 3 minutes of connection).
				println("In Coverage " + outStr);
				break;
			case APP_ACK:
				// The terminal send the Acknowledgement message to acknowledge the reception of a 
				// Host message that need to be acknowledged.
				println("In ACK " + outStr);
				break;
			case APP_IP_CHANGED:
				// The unit sends the message when the unit receives new IP from the network.
				println("IP Changed " + outStr);
				break;							
			default:
				println("Unknown message #" + msgType + ", " +  outStr);
				break;
				
		}
	}	
	
	/**
	 * function  parseMessage
	 * This function parse the class of a received message, and calls the appropriate 
	 * function to parse it.
	 * First message from the unit must be presentation message - if not, 
	 * the connection is aborted
	 */
	private void parseMessage(int message[], int messageSize) throws Exception{
		switch(message[0]){
		case APP_CLASS:
			if(unitID == null) {
				// We wont accept any message from unsolicited unit
				// Abort connection...
				throw new Exception("Unit must first identify itself with presentation message."); 
			}
			parseAppMsg(message, messageSize);
			break;
		case PRESENTATION_CLASS:
			parsePresentationMsg(message, messageSize);
			if(unitID != null) {
				addToHash(unitID, this);
			}
			break;
		case KEEP_ALIVE_CLASS:
			if(unitID == null) {
				// We wont accept any message from unsolicited unit
				// Abort connection...
				throw new Exception("Unit must first identify itself with presentation message."); 
			}
			println("Keep Alive message...");
			break;
		default:
			println("Unknown message class: " + message[0]);
			break;
		}
	}

	/**
	 * function  run
	 * This function is the one activated when the <TCPServer> class activates the <TCPUnitConnection>
	 * thread.
	 * Once the <run> function of the TCPUnitConnection thread is started, it opens a buffered 
	 * input stream to read messages from the connected unit.
 	 * Note that it is preferable to use Buffered input stream and not a raw stream, as it requires 
 	 * much less resources from the server (much less IO operations).
 	 * The <run> function reads from the input stream until it gets the start of message char (STX), 
 	 * than it reads the message into a buffer until it receives the end message char (ETX).
 	 * Note that while reading the message, the <run> function needs also to replace "escaped characters"
 	 * with the original characters (for details see section 6.2 in the TCP/IP interface guide).
 	 * Once a message is read, the run function calls the parseMessage function.
	 */
	public void run() {
	    int buf[] = new int[MAX_WLI_PCK_SIZE];
		int bufIdx = 0;
    	int c;

    	try {
    		// Create the buffered input stream
	        BufferedInputStream inputStream = new BufferedInputStream(connectionSocket.getInputStream());
	        while(true)
	        {
	        	bufIdx = 0;
	            // Wait for start of message 
	        	// All other characters are dropped
		        do {
					c = inputStream.read();
		        } while(c != STX_START_WLI_PCK);
		        
		        String unit = "";
		        if(unitID != null) {
		        	unit= " (Unit " + unitID + ")";
		        }
		        print("Message" + unit + ": ");
		        
		        // Read the rest of the message - until you get to the ETX character
		        do {
		        	c = inputStream.read();
		        	if(c == ETX_END_WLI_PCK){
		        		break;
		        	}
		        	// Replace escaped characters with the original ones
		        	if(c==ESCAPE_HDR) {
		        		int c1 = inputStream.read();
		        		switch(c1) {
			        		case ESCAPE_STX:
			        			c = STX_START_WLI_PCK;
			        			break;
			        		case ESCAPE_ETX:
			        			c = ETX_END_WLI_PCK;
			        			break;
			        		case ESCAPE_DB:
			        			c = ESCAPE_HDR;
			        			break;
		        		}
		        	}
		        	buf[bufIdx++] = c;
		        	print("<0x"+Integer.toHexString(c)+">");
		        } while(true);
		        println(" :END");
		        // Handle the received message.
	        	parseMessage(buf, bufIdx);
	        }
		} catch (Exception e) {
			// An error occurred, close connections and exit the thread loop.
			if(connectionSocket != null) {
		        println(">>> Closing connection & thread, UnitID:" + unitID + ", Reason: " + e.getMessage());	        
				try {
					connectionSocket.close();
				} catch (IOException e1) {
					connectionSocket = null;
				}
			} else { 
				println(">>> Closing thread, UnitID:" + unitID + ", Reason: " + e.getMessage());	        
			}
		}
    }   
}
