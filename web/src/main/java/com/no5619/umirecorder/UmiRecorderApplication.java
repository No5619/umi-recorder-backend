package com.no5619.umirecorder;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.no5619.*")
@SpringBootApplication
public class UmiRecorderApplication {
    public static void main(String[] args) {
        SpringApplication.run(UmiRecorderApplication.class, args);
    }
}