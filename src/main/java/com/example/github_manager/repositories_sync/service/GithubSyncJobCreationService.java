package com.example.github_manager.repositories_sync.service;

import com.example.github_manager.repositories_sync.entity.GithubSyncJob;
import com.example.github_manager.repositories_sync.repositories.GithubSyncJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class GithubSyncJobCreationService {
    private final GithubSyncJobRepository githubSyncJobRepository;
    private final Clock clock;

    public Long createSyncJob() {
        return createNewSyncJob();
    }

    private Long createNewSyncJob() {
        GithubSyncJob githubSyncJob = new GithubSyncJob();
        githubSyncJob.setNextRunAt(Instant.now(clock));
        return githubSyncJobRepository.save(githubSyncJob).getId();
    }

}