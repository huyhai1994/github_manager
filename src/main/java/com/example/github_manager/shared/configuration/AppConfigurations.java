package com.example.github_manager.shared.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class AppConfigurations {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
