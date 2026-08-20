package com.example.github_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class GithubManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GithubManagerApplication.class, args);
    }

}
