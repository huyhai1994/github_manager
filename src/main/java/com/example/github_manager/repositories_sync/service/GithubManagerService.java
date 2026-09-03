package com.example.github_manager.repositories_sync.service;

import com.example.github_manager.repositories_sync.entity.GithubSyncJob;
import com.example.github_manager.repositories_sync.repositories.GithubSyncJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GithubManagerService {
    private final GithubSyncJobRepository githubSyncJobRepository;

    public Long createSyncJob() {
        return createNewSyncJob();
    }

    private Long createNewSyncJob() {
        return githubSyncJobRepository.save(new GithubSyncJob()).getId();
    }

}