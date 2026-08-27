package com.example.github_manager.repositories_sync.service;

import com.example.github_manager.repositories_sync.component.GitHubRestClient;
import com.example.github_manager.repositories_sync.component.GithubResponseDeserializer;
import com.example.github_manager.repositories_sync.dto.GithubPageResponse;
import com.example.github_manager.repositories_sync.dto.GithubRawResponse;
import com.example.github_manager.repositories_sync.repositories.GithubRepositoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import support.mock.MockGithubRepositoryResponse;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GithubPageSyncingServiceMockTest {

    @Mock
    GitHubRestClient gitHubRestClient;

    @Mock
    GithubResponseDeserializer githubResponseDeserializer;

    @Mock
    GithubRepositoryRepository githubRepositoryRepository;

    @InjectMocks
    GithubPageSyncingService githubPageSyncingService;

    @Test
    void syncAllPages_whenFetchAndDeserializePageSucceed_thenSaveToDB() {
        // arrange
        when(gitHubRestClient.getOwnedRepositories(anyInt(), anyInt())).thenReturn(new GithubRawResponse(200, null, "sample"));
        GithubPageResponse page1 = new GithubPageResponse(MockGithubRepositoryResponse.repositories());
        GithubPageResponse emptyPage = new GithubPageResponse(MockGithubRepositoryResponse.emptyRepositories());

        when(githubResponseDeserializer.deserialize(any(GithubRawResponse.class)))
                .thenReturn(page1, emptyPage);
        // act
        githubPageSyncingService.syncAllPages();

        // verify
        verify(githubRepositoryRepository, times(1)).saveAll(any());
    }

    @Test
    void syncAllPages_whenPageIsEmpty_thenDoNotSaveToDB() {
        // arrange
        when(gitHubRestClient.getOwnedRepositories(anyInt(), anyInt())).thenReturn(new GithubRawResponse(200, null, "sample"));
        GithubPageResponse emptyPage = new GithubPageResponse(MockGithubRepositoryResponse.emptyRepositories());

        when(githubResponseDeserializer.deserialize(any(GithubRawResponse.class)))
                .thenReturn(emptyPage);
        // act
        githubPageSyncingService.syncAllPages();

        // verify
        verifyNoInteractions(githubRepositoryRepository);
    }
}