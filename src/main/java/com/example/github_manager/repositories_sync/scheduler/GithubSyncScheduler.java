package com.example.github_manager.repositories_sync.scheduler;

import com.example.github_manager.repositories_sync.component.GithubSyncStateManager;
import com.example.github_manager.repositories_sync.component.GithubSyncWorker;
import com.example.github_manager.repositories_sync.repositories.GithubSyncJobRepository;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class GithubSyncScheduler {

    private final GithubSyncJobRepository githubSyncJobRepository;
    private final Clock clock;
    private final GithubSyncWorker githubSyncWorker;
    private final GithubSyncStateManager githubSyncStateManager;

    @Scheduled(fixedDelayString = "${github-sync-scheduler.delay}", timeUnit = TimeUnit.MILLISECONDS)
    @WithSpan("github-sync-scheduler-repo-syncing")
    public void repoSyncing() {
        githubSyncJobRepository
                .findDueReadyJob(Instant.now(clock))
                .ifPresent(
                        (id) -> {
                            githubSyncStateManager.changeStateFromReadyToSubmitted(id);
                            githubSyncWorker.sync(id);
                        }
                );
    }
}
