package com.example.github_manager.repositories_sync.component;

import com.example.github_manager.repositories_sync.dto.GithubPageResponse;
import com.example.github_manager.repositories_sync.dto.GithubRawResponse;
import com.example.github_manager.repositories_sync.dto.GithubRepositoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JacksonGithubResponseDeserializer implements GithubResponseDeserializer {

    private final ObjectMapper objectMapper;

    @Override
    public GithubPageResponse deserialize(GithubRawResponse gitHubRawResponse) {
        String rawBody = gitHubRawResponse.body();
        Objects.requireNonNull(rawBody);
        List<GithubRepositoryResponse> repositories = objectMapper.readValue(rawBody, new TypeReference<>() {
        });
        return GithubPageResponse.builder()
                .repositories(repositories).build();
    }
}
