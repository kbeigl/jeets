package org.jeets.device;

public final class Main {

    public Main() {}

    public static void main(String[] args) throws Exception {
        org.apache.camel.spring.Main main = new org.apache.camel.spring.Main();
        main.setApplicationContextUri("META-INF/spring/device.xml");
        main.run();
    }

}
