package com.example.github_manager.get_metadata.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GithubRepositoryResponse(

        Long id,

        String name,

        String fullName,

        GithubOwnerResponse owner,

        String htmlUrl,

        String visibility,

        Instant createdAt,

        Instant updatedAt,

        Instant pushedAt,

        @JsonProperty("private")
        boolean privateRepository


) {
}
