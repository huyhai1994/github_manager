package com.example.github_manager.repositories_sync.component;

import com.example.github_manager.repositories_sync.service.GithubPageSyncingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class GithubSyncWorkerMockTest {

    @InjectMocks
    GithubSyncWorker githubSyncWorker;

    @Mock
    GithubSyncStateManager githubSyncStateManager;

    @Mock
    GithubPageSyncingService githubPageSyncingService;

    @Test
    void sync_whenSyncAllPagesSucceed_thenScheduleNextRun() {
        
    }

}