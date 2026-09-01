package com.example.github_manager.repositories_sync.component;

import com.example.github_manager.repositories_sync.exception.InvalidJobStateTransitionException;
import com.example.github_manager.repositories_sync.service.GithubPageSyncingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GithubSyncWorker {

    private final GithubSyncStateManager githubSyncStateManager;
    private final GithubPageSyncingService githubPageSyncingService;

    @Async("githubSyncExecutor")
    public void sync(Long jobId) {
        try {
            startSyncing(jobId);
            try {
                syncAllPages();
            } catch (Exception e) {
                log.error("SYNC_ERROR ex={}", e.getMessage());
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
