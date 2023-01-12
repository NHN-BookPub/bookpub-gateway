package com.nhnacademy.bookpubgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;


@SpringBootApplication
@ConfigurationPropertiesScan
public class BookpubGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookpubGatewayApplication.class, args);
    }

}
