package com.example.github_manager.repositories_sync.dto;

import org.springframework.http.HttpHeaders;

public record GithubRawResponse(
        int statusCode,
        HttpHeaders headers,
        String body
) {
}
