package com.example.github_manager.repositories_sync.service;

import com.example.github_manager.repositories_sync.entity.GithubRepository;
import com.example.github_manager.repositories_sync.repositories.GithubRepositoryRepository;
import com.example.github_manager.repositories_sync.repositories.GithubSyncJobRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import support.AbstractIntegrationTest;
import support.mock_server.MockServerSupport;


import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(MockServerSupport.class)
class GithubPageSyncingServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    GithubPageSyncingService githubPageSyncingService;

    @Autowired
    GithubSyncJobRepository githubSyncJobRepository;

    @Autowired
    GithubRepositoryRepository githubRepositoryRepository;

    @Autowired
    MockServerSupport.GithubMockServer githubMockServer;

    @BeforeEach
    void setUp() {
        githubMockServer.setUp();
    }

    @AfterEach
    void tearDown() {
        githubMockServer.tearDown();
        githubSyncJobRepository.deleteAllInBatch();
        githubRepositoryRepository.deleteAllInBatch();
    }

    @Test
    void syncAllPages_whenFetchAllPages_thenSaveAllToDB() {
        githubPageSyncingService.syncAllPages();
        List<GithubRepository> githubRepositories = githubRepositoryRepository.findAll();
        assertThat(githubRepositories.size()).isNotZero();
    }
}