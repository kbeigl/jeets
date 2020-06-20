package org.jeets.dcs.string;

import org.springframework.stereotype.Service;

@Service  // register in Spring context as a bean
public class EchoService {
    public String sayHello(String guestName) {
        return "ACK: " + guestName;
    }
}
