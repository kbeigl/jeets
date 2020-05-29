package org.jeets.device;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// import org.springframework.context.annotation.ComponentScan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// @ComponentScan(basePackages="org.jeets.device")
public class Main {

//  private Main() {};

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LOGGER.info("run SpringApplication ...");
        SpringApplication.run(Main.class, args);
    }

}
