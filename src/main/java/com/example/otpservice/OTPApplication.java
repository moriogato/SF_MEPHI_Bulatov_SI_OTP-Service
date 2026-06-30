package com.example.otpservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OTPApplication {
    public static void main(String[] args) {
        SpringApplication.run(OTPApplication.class, args);
    }
}