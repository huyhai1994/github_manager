package com.example.github_manager.repositories_sync.component;

import com.example.github_manager.repositories_sync.exception.InvalidJobStateTransitionException;
import com.example.github_manager.repositories_sync.service.GithubPageSyncingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.JacksonException;

import static org.mockito.Mockito.*;


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
        githubSyncWorker.sync(1L);
        verify(githubSyncStateManager).changeStateFromSubmittedToSyncing(eq(1L));
        verify(githubSyncStateManager).changeStateFromSyncingToReady(eq(1L));
        verify(githubSyncStateManager, never()).changeStateFromSyncingToFailed(eq(1L));
    }

    @Test
    void sync_whenCouldNotSerializePage_thenHandleFailure() {
        doThrow(JacksonException.class)
                .when(githubPageSyncingService)
                .syncAllPages();

        githubSyncWorker.sync(1L);

        verify(githubSyncStateManager).changeStateFromSubmittedToSyncing(eq(1L));
        verify(githubSyncStateManager, never()).changeStateFromSyncingToReady(eq(1L));
        verify(githubSyncStateManager).changeStateFromSyncingToFailed(eq(1L));
    }

    @Test
    void sync_whenInvalidStateTransition_thenNotSyncingPage() {
        doThrow(InvalidJobStateTransitionException.class)
                .when(githubSyncStateManager)
                .changeStateFromSubmittedToSyncing(1L);

        githubSyncWorker.sync(1L);

        verifyNoInteractions(githubPageSyncingService);
    }
}