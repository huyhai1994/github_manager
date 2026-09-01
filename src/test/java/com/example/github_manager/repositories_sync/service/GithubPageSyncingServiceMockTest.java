package com.example.github_manager.repositories_sync.service;

import com.example.github_manager.repositories_sync.component.GithubServiceRestClient;
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
import tools.jackson.core.JacksonException;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GithubPageSyncingServiceMockTest {

    @Mock
    GithubServiceRestClient gitHubServiceRestClient;

    @Mock
    GithubResponseDeserializer githubResponseDeserializer;

    @Mock
    GithubRepositoryRepository githubRepositoryRepository;

    @Mock
    GithubRepoUpsertService githubRepoUpsertService;

    @InjectMocks
    GithubPageSyncingService githubPageSyncingService;

    @Test
    void syncAllPages_whenFetchAndDeserializePageSucceed_thenSaveToDB() {
        // arrange
        when(gitHubServiceRestClient.getOwnedRepositories(anyInt())).thenReturn(new GithubRawResponse(200, null, "sample"));
        GithubPageResponse page1 = new GithubPageResponse(MockGithubRepositoryResponse.repositories());
        GithubPageResponse emptyPage = new GithubPageResponse(MockGithubRepositoryResponse.emptyRepositories());

        when(githubResponseDeserializer.deserialize(any(GithubRawResponse.class)))
                .thenReturn(page1, emptyPage);
        // act
        githubPageSyncingService.syncAllPages();

        // verify
        verify(githubRepoUpsertService, times(1)).upsertAll(any());
    }

    @Test
    void syncAllPages_whenPageIsEmpty_thenDoNotSaveToDB() {
        // arrange
        when(gitHubServiceRestClient.getOwnedRepositories(anyInt())).thenReturn(new GithubRawResponse(200, null, "sample"));
        GithubPageResponse emptyPage = new GithubPageResponse(MockGithubRepositoryResponse.emptyRepositories());

        when(githubResponseDeserializer.deserialize(any(GithubRawResponse.class)))
                .thenReturn(emptyPage);
        // act
        githubPageSyncingService.syncAllPages();

        // verify
        verifyNoInteractions(githubRepositoryRepository);
    }

    @Test
    void syncAllPages_whenThrowJacksonException_thenDoNotSaveDB() {
        // arrange
        when(gitHubServiceRestClient.getOwnedRepositories(anyInt())).thenReturn(new GithubRawResponse(200, null, "sample"));

        when(githubResponseDeserializer.deserialize(any(GithubRawResponse.class))).thenThrow(JacksonException.class);
        // act
        assertThrows(JacksonException.class, () -> githubPageSyncingService.syncAllPages());

        // verify
        verifyNoInteractions(githubRepositoryRepository);
    }
}