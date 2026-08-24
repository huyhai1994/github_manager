package com.example.github_manager.repositories_sync.component;

import com.example.github_manager.repositories_sync.dto.GithubRawResponse;
import com.example.github_manager.repositories_sync.dto.GithubPageResponse;

public interface GithubResponseDeserializer {
    GithubPageResponse deserialize(GithubRawResponse gitHubRawResponse);
}
