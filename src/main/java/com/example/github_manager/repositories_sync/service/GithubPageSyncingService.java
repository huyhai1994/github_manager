package com.example.github_manager.repositories_sync.service;

import com.example.github_manager.repositories_sync.component.GithubServiceRestClient;
import com.example.github_manager.repositories_sync.component.GithubResponseDeserializer;
import com.example.github_manager.repositories_sync.dto.GithubPageResponse;
import com.example.github_manager.repositories_sync.dto.GithubRawResponse;
import com.example.github_manager.repositories_sync.dto.GithubRepositoryResponse;
import com.example.github_manager.repositories_sync.dto.RepositoriesState;
import com.example.github_manager.repositories_sync.entity.GithubRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubPageSyncingService {
    private final GithubServiceRestClient gitHubServiceRestClient;
    private final GithubResponseDeserializer githubResponseDeserializer;
    private final GithubRepoUpsertService githubRepoUpsertService;

    public void syncAllPages() {
        int page = 0;
        while (true) {
            GithubPageResponse githubPageResponses = getGithubPageResponses(page);
            if (isEmpty(githubPageResponses)) break;
            upsertAll(githubPageResponses);
            page++;
        }
    }

    private static boolean isEmpty(GithubPageResponse githubPageResponses) {
        return githubPageResponses.repositories().isEmpty();
    }

    private GithubPageResponse getGithubPageResponses(int page) {
        GithubRawResponse githubRawResponses = gitHubServiceRestClient.getOwnedRepositories(page);
        log.debug("GET_RAW_RESPONSE = {}", githubRawResponses.toString());
        return githubResponseDeserializer.deserialize(githubRawResponses);
    }

    private void upsertAll(GithubPageResponse githubPageResponses) {
        githubRepoUpsertService.upsertAll(
                githubPageResponses
                        .repositories()
                        .stream()
                        .map(this::createGithubRepository)
                        .toList());
    }

    private GithubRepository createGithubRepository(GithubRepositoryResponse r) {
        GithubRepository githubRepository = new GithubRepository();
        githubRepository.setGithubId(r.id());
        githubRepository.setFullName(r.fullName());
        githubRepository.setOwnerLogin(r.owner().login());
        githubRepository.setHtmlUrl(r.htmlUrl());
        githubRepository.setGithubCreatedAt(r.createdAt());
        githubRepository.setGithubUpdatedAt(r.updatedAt());
        githubRepository.setGithubPushedAt(r.pushedAt());
        githubRepository.setStatus(RepositoriesState.SYNCING);
        githubRepository.setIsPrivate(r.privateRepository());
        return githubRepository;
    }


}
