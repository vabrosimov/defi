package ru.abrosimov.defi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DefiApplication {
    public static void main(String[] args) {
        SpringApplication.run(DefiApplication.class, args);
    }
}

