package com.example.github_manager.repositories_sync.component;

import com.example.github_manager.repositories_sync.exception.InvalidJobStateTransitionException;
import com.example.github_manager.repositories_sync.service.GithubPageSyncingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;

@Component
@RequiredArgsConstructor
@Slf4j
public class GithubSyncWorker {

    private final GithubSyncStateManager githubSyncStateManager;
    private final GithubPageSyncingService githubPageSyncingService;

    public void sync(Long jobId) {
        try {
            startSyncing(jobId);
            try {
                syncAllPages();
            } catch (JacksonException e) {
                handleSyncFailure(jobId);
                return;
            }
            scheduleNextRun(jobId);
        } catch (InvalidJobStateTransitionException e) {
            log.debug("CLAIM_JOB_FAILED jobId={}", jobId);
        }
    }

    private void handleSyncFailure(Long jobId) {
        githubSyncStateManager.changeStateFromSyncingToFailed(jobId);
    }

    private void syncAllPages() {
        githubPageSyncingService.syncAllPages();
    }

    private void scheduleNextRun(Long jobId) {
        githubSyncStateManager.changeStateFromSyncingToReady(jobId);
    }

    private void startSyncing(Long jobId) {
        githubSyncStateManager.changeStateFromSubmittedToSyncing(jobId);
    }
}
