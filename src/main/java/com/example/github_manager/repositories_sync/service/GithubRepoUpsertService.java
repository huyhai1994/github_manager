package com.example.github_manager.repositories_sync.service;

import com.example.github_manager.repositories_sync.entity.GithubRepository;
import com.example.github_manager.repositories_sync.repositories.GithubRepositoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GithubRepoUpsertService {

    private final GithubRepositoryRepository githubRepositoryRepository;

    @Transactional
    public void upsertAll(List<GithubRepository> incomingRepositories) {
        List<Long> githubIds = getIncomingRepositoryIds(incomingRepositories);
        Map<Long, GithubRepository> existingById = getExistingById(githubIds);
        Map<Boolean, List<GithubRepository>> partitioned = incomingRepositories
                .stream()
                .collect(Collectors.partitioningBy(
                        incomingRepository -> {
                            return !existingById.containsKey(incomingRepository.getGithubId());
                        }));
        partitioned.get(false).forEach(
                incoming -> {
                    updateExisting(existingById.get(incoming.getGithubId()), incoming);
                });
        List<GithubRepository> newRepositories = partitioned.get(true);
        if (!newRepositories.isEmpty()) {
            githubRepositoryRepository.saveAll(newRepositories);
        }
    }

    private Map<Long, GithubRepository> getExistingById(List<Long> githubIds) {
        return githubRepositoryRepository.findAllById(githubIds)
                .stream()
                .collect(Collectors.toMap(
                        GithubRepository::getGithubId,
                        Function.identity()
                ));
    }

    private List<Long> getIncomingRepositoryIds(List<GithubRepository> incomingRepositories) {
        return incomingRepositories.stream()
                .map(GithubRepository::getGithubId)
                .distinct()
                .toList();
    }

    private void updateExisting(
            GithubRepository existing,
            GithubRepository incoming
    ) {
        existing.setOwnerLogin(incoming.getOwnerLogin());
        existing.setFullName(incoming.getFullName());
        existing.setHtmlUrl(incoming.getHtmlUrl());
        existing.setGithubUpdatedAt(incoming.getGithubUpdatedAt());
        existing.setGithubCreatedAt(incoming.getGithubCreatedAt());
        existing.setGithubPushedAt(incoming.getGithubPushedAt());
        existing.setIsPrivate(incoming.getIsPrivate());
    }

}

