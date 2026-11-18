package com.example.sendemail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SendEmailApplication {

    public static void main(String[] args) {
        // Run the CLI, then close the context and exit with the runner's exit code.
        System.exit(SpringApplication.exit(SpringApplication.run(SendEmailApplication.class, args)));
    }
}
