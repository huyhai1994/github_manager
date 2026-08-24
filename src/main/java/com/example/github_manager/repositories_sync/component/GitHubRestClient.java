package com.example.github_manager.repositories_sync.component;

import com.example.github_manager.repositories_sync.dto.GitHubRawResponse;
import com.example.github_manager.repositories_sync.dto.GithubRepositoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GitHubRestClient {
    private final RestClient restClient;
    private static final MediaType GITHUB_MEDIA_TYPE =
            MediaType.parseMediaType("application/vnd.github+json");

    public ResponseEntity<List<GithubRepositoryResponse>> getOwnedRepositories(
            int page,
            int pageSize
    ) {

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/user/repos")
                        .queryParam("affiliation", "owner")
                        .queryParam("per_page", pageSize)
                        .queryParam("page", page)
                        .build())
                .accept(GITHUB_MEDIA_TYPE)
                .header("X-GitHub-Api-Version", "2022-11-28")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }


}
