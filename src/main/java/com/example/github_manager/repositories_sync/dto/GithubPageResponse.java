package com.example.github_manager.repositories_sync.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record GithubPageResponse(
        List<GithubRepositoryResponse> repositories
) {
}
