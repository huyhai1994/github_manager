package com.example.github_manager.repositories_sync.configuration.rest_client;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("clients.github-service")
public record GithubRestClientProperties(
        String baseUrl,
        String getRepositoriesPath,
        String repos,
        Duration connectTimeout,
        Duration readTimeout,
        String accessToken,
        Integer pageSize,
        String mediaType,
        String path,
        String affiliation,
        String apiVersion

) {
}
