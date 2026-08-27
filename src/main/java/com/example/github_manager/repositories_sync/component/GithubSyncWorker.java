package com.example.github_manager.repositories_sync.component;

import com.example.github_manager.repositories_sync.service.GithubPageSyncingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GithubSyncWorker {

    private final GithubSyncStateManager githubSyncStateManager;
    private final GithubPageSyncingService githubPageSyncingService;

    public void sync(Long jobId) {
        startSyncing(jobId);
        try {
            syncAllPages();
        } catch (Exception e) {
            handleSyncFailure(jobId);
        }
        scheduleNextRun(jobId);
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
