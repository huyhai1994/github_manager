package com.example.github_manager.get_metadata.component;

import com.example.github_manager.get_metadata.dto.GithubRepositoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GitHubRestClient {
    private final RestClient restClient;

    public List<GithubRepositoryResponse> getOwnedRepositories(
            int page,
            int pageSize
    ) {
        List<GithubRepositoryResponse> repositories = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/user/repos")
                        .queryParam("affiliation", "owner")
                        .queryParam("per_page", pageSize)
                        .queryParam("page", page)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        return repositories == null ? List.of() : repositories;
    }


}
