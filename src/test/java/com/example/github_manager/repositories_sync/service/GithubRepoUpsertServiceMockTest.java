package com.example.github_manager.repositories_sync.service;

import com.example.github_manager.repositories_sync.entity.GithubRepository;
import com.example.github_manager.repositories_sync.repositories.GithubRepositoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import support.mock.MockGithubRepositoryEntity;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GithubRepoUpsertServiceMockTest {
    @InjectMocks
    GithubRepoUpsertService githubRepoUpsertService;

    @Mock
    GithubRepositoryRepository githubRepositoryRepository;

    @Captor
    ArgumentCaptor<List<GithubRepository>> argumentCaptor;

    @Test
    @DisplayName("when there are new repositories, verify saving new repositories, not update")
    void upsert_saveNewRepositories() {
        List<GithubRepository> incomingRepositories = new ArrayList<>();
        incomingRepositories.add(MockGithubRepositoryEntity.Entity1.create());
        incomingRepositories.add(MockGithubRepositoryEntity.Entity2.create());

        when(githubRepositoryRepository
                .findAllById(
                        List.of(MockGithubRepositoryEntity.Entity1.GITHUB_ID
                                , MockGithubRepositoryEntity.Entity2.GITHUB_ID)))
                .thenReturn(List.of());
        githubRepoUpsertService.upsertAll(incomingRepositories);

        verify(githubRepositoryRepository)
                .saveAll(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("when there are new repo and existing repo, then save new repository, and update existing repository")
    void upsert_saveNewRepoAndUpdateExisting() {
        List<GithubRepository> incomingRepositories = new ArrayList<>();

        GithubRepository newRepo = MockGithubRepositoryEntity.Entity1.create();
        GithubRepository incomingExistingRepo = MockGithubRepositoryEntity.Entity2.create();
        GithubRepository existingRepo = MockGithubRepositoryEntity.Entity2.create();

        existingRepo.setFullName("old-name");
        incomingExistingRepo.setFullName("new-name");

        incomingRepositories.add(newRepo);
        incomingRepositories.add(incomingExistingRepo);

        when(githubRepositoryRepository
                .findAllById(
                        List.of(newRepo.getGithubId(), incomingExistingRepo.getGithubId()
                        )))
                .thenReturn(
                        List.of(existingRepo));
        githubRepoUpsertService.upsertAll(incomingRepositories);

        ArgumentCaptor<List<GithubRepository>> argumentCaptor = ArgumentCaptor
                .forClass(List.class);

        verify(githubRepositoryRepository, times(1))
                .saveAll(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue()).containsExactly(newRepo);

        assertThat(existingRepo.getFullName()).isEqualTo("new-name");
    }

    @Test
    @DisplayName("when there is updating, verify updating existing, not save new repository")
    void update_updateExistingRepositories() {
        List<GithubRepository> incomingRepositories = new ArrayList<>();
        incomingRepositories.add(MockGithubRepositoryEntity.Entity1.create());
        incomingRepositories.add(MockGithubRepositoryEntity.Entity2.create());

        when(githubRepositoryRepository
                .findAllById(
                        List.of(MockGithubRepositoryEntity.Entity1.GITHUB_ID
                                , MockGithubRepositoryEntity.Entity2.GITHUB_ID)))
                .thenReturn(
                        List.of(MockGithubRepositoryEntity.Entity1.create(),
                                MockGithubRepositoryEntity.Entity2.create())
                );
        githubRepoUpsertService.upsertAll(incomingRepositories);

        ArgumentCaptor<List<GithubRepository>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        verify(githubRepositoryRepository, never())
                .saveAll(argumentCaptor.capture());

    }

}