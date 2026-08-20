package com.example.github_manager;

import org.springframework.boot.SpringApplication;

public class TestGithubManagerApplication {

    public static void main(String[] args) {
        SpringApplication.from(GithubManagerApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
