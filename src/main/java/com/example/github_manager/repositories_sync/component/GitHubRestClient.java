package com.example.github_manager.repositories_sync.component;

import com.example.github_manager.repositories_sync.configuration.rest_client.GithubRestClientProperties;
import com.example.github_manager.repositories_sync.dto.GithubRawResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class GitHubRestClient {
    private final RestClient restClient;
    private static final MediaType GITHUB_MEDIA_TYPE =
            MediaType.parseMediaType("application/vnd.github+json");
    private final GithubRestClientProperties githubRestClientProperties;

    public GithubRawResponse getOwnedRepositories(
            int page
    ) {
        ResponseEntity<String> responseEntity = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/user/repos")
                        .queryParam("affiliation", "owner")
                        .queryParam("per_page", githubRestClientProperties.pageSize())
                        .queryParam("page", page)
                        .build())
                .accept(GITHUB_MEDIA_TYPE)
                .header("X-GitHub-Api-Version", "2022-11-28")
                .retrieve()
                .toEntity(String.class);

        HttpHeaders headers = new HttpHeaders();
        headers.putAll(responseEntity.getHeaders());

        return new GithubRawResponse(
                responseEntity.getStatusCode().value(),
                headers,
                responseEntity.getBody());
    }


}
