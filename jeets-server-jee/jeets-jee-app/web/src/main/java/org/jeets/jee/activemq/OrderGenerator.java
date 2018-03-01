package org.jeets.jee.activemq;

import java.util.Random;

import javax.inject.Named;

import org.apache.camel.CamelContext;

@Named
public class OrderGenerator {

    public static final String[] COUNTRIES = {"UK", "US", "Other"};

    private int count = 1;
    private Random random = new Random();

    public String generateOrderString(CamelContext camelContext) {
        String countryCode = COUNTRIES[random.nextInt(3)];
        return countryCode;
    }

}
