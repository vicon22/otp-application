package com.eveiled.otp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class OtpApplication {

    public static void main(String[] args) {
        SpringApplication.run(OtpApplication.class, args);
    }

}
