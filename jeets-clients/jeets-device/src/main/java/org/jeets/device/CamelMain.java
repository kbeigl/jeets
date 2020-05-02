package org.jeets.device;

//import org.jeets.dcs.traccar.Main;
//import org.springframework.boot.SpringApplication;

public final class CamelMain {

    public CamelMain() {}
    
//  see dcs.traccar.Main, and javadoc:
//    SpringApplication.run(Main.class, args);

    public static void main(String[] args) throws Exception {
        org.apache.camel.spring.Main main = new org.apache.camel.spring.Main();
        main.setApplicationContextUri("META-INF/spring/device.xml");
        main.run();
        main.close(); // reached?
    }

}
