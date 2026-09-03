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
public class GithubServiceRestClient {
    private final RestClient restClient;
    private final GithubRestClientProperties githubRestClientProperties;
    private final String AFFILIATION_PARAM = "affiliation";
    private final String PER_PAGE_PARAM = "per_page";
    private final String PAGE_PARAM = "page";
    private final String API_VERSION = "X-GitHub-Api-Version";

    public GithubRawResponse getOwnedRepositories(
            int page
    ) {
        ResponseEntity<String> responseEntity = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(githubRestClientProperties.path())
                        .queryParam(AFFILIATION_PARAM, githubRestClientProperties.affiliation())
                        .queryParam(PER_PAGE_PARAM, githubRestClientProperties.pageSize())
                        .queryParam(PAGE_PARAM, page)
                        .build())
                .accept(MediaType.parseMediaType(githubRestClientProperties.mediaType()))
                .header(API_VERSION, githubRestClientProperties.apiVersion())
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
