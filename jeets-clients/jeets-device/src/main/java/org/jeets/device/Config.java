package org.jeets.device;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);

    @Bean(name = "Device")
    public Device createDevice() {
        System.out.println("create Device @Bean");
        return new Device();
    }


}
