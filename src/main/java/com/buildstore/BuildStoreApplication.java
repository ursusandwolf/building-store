package com.buildstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BuildStoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(BuildStoreApplication.class, args);
    }
}
