package org.jeets.device;

import org.apache.camel.Consume;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parse different line formats to prepare 'device.send' method. Text line can
 * be any source, i.e. split lines from a text file or enter line to a stream
 * input etc.
 */
public class LineParser {

    private static final Logger log = LoggerFactory.getLogger(LineParser.class);

    /**
     * File type One has four columns 'protocol host port sync hexmsg' separated by
     * tabs. The textline is parsed into the DeviceConfig, which is added as a
     * header to be applied by the Device for sending the hex message.
     */
    @Consume(uri = "direct:setconf.fileone")
    public void parseFileOne( Exchange exchange ) throws Throwable {

        Message msg = exchange.getIn();
//      see File Component to extract oneLine
        String line = ((String) msg.getBody()).trim();
        if (line.startsWith("#")) {
//          TODO add check for empty lines
            return;
        }
        log.debug("parseFileOne( " + line + " )");

//      TODO Exception handling
        {
            String[] attribs = line.split("\\t");
//          expected attribute order, i.e. file format
//          protocol  host  port  sync  hexmsg
            DeviceConfig config = new DeviceConfig();
            config.setProtocol(attribs[0]);
            config.setHost(attribs[1]);
            config.setPort(Integer.parseInt(attribs[2]));
            config.setSync(Boolean.parseBoolean(attribs[3]));

            if (attribs[4].endsWith("\r"))
                msg.setBody(attribs[4].substring(0, attribs[4].length() - 1)); 
            else
                msg.setBody(attribs[4]); 
//          check if valid > mark bad messages ?

            msg.setHeader("DeviceConfig", config);
        }
    }
}
