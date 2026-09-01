package com.example.github_manager.repositories_sync.component;

import com.example.github_manager.repositories_sync.exception.InvalidJobStateTransitionException;
import com.example.github_manager.repositories_sync.repositories.GithubSyncJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class GithubSyncStateManager {

    private final GithubSyncJobRepository githubSyncJobRepository;
    private final Clock clock;

    @Transactional
    public void changeStateFromReadyToSubmitted(Long jobId) {
        int affectedRow = githubSyncJobRepository.markSubmittedFromReady(jobId, Instant.now(clock));
        checkJobState(affectedRow);
    }

    @Transactional
    public void changeStateFromSubmittedToSyncing(Long jobId) {
        int affectedRow = githubSyncJobRepository.markSyncingFromSubmitted(jobId, Instant.now(clock));
        checkJobState(affectedRow);
    }

    @Transactional
    public void changeStateFromSyncingToReady(Long jobId) {
        Instant now = Instant.now(clock);
        int affectedRow = githubSyncJobRepository.markReadyFromSyncing(jobId, now, now.plus(1, ChronoUnit.HOURS));
        checkJobState(affectedRow);
    }

    @Transactional
    public void changeStateFromSyncingToFailed(Long jobId) {
        Instant now = Instant.now(clock);
        int affectedRow = githubSyncJobRepository.markSyncFailedFromSyncing(jobId, now);
        checkJobState(affectedRow);
    }

    private void checkJobState(int affectedRow) {
        if (affectedRow == 0) throw new InvalidJobStateTransitionException();
    }
}
