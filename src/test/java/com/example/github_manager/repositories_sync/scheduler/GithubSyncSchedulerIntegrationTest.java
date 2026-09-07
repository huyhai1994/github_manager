package com.example.github_manager.repositories_sync.scheduler;

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
import support.clock.ClockConfiguration;
import support.mock.MockGithubJobEntity;
import support.mock_server.MockServerSupport;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@ActiveProfiles("test")
@Import({MockServerSupport.class, ClockConfiguration.class})
class GithubSyncSchedulerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    MockServerSupport.GithubMockServer githubMockServer;

    @Autowired
    GithubSyncJobRepository githubSyncJobRepository;

    @Autowired
    GithubRepositoryRepository githubRepositoryRepository;

    @Autowired
    GithubSyncScheduler githubSyncScheduler;

    @Autowired
    private Clock fixedClock;

    @BeforeEach
    void setup() {
        githubMockServer.setUp();
    }

    @AfterEach
    void tearDown() {
        githubMockServer.tearDown();
        githubSyncJobRepository.deleteAllInBatch();
        githubRepositoryRepository.deleteAllInBatch();
    }

    @Test
    void repoSyncing_whenThereIsAnDueReadyJob_thenProcessSyncing() {
        assertThat(githubRepositoryRepository.findAll())
                .isEmpty();
        githubSyncJobRepository
                .save(MockGithubJobEntity.createReadyJob());
        await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    githubSyncScheduler.repoSyncing();
                    assertThat(githubRepositoryRepository.findAll().size()).isNotZero();
                    assertThat(
                            githubSyncJobRepository.findById(1L)
                                    .orElseThrow().getNextRunAt())
                            .isEqualTo(Instant.now(fixedClock)
                                    .plus(1, ChronoUnit.HOURS));
                });
    }


}