package com.example.github_manager.repositories_sync.scheduler;

import com.example.github_manager.repositories_sync.repositories.GithubRepositoryRepository;
import com.example.github_manager.repositories_sync.repositories.GithubSyncJobRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import support.AbstractIntegrationTest;
import support.clock.ClockConfiguration;
import support.concurency.RaceConditionSimulator;
import support.concurency.TaskResult;
import support.mock.MockGithubJobEntity;
import support.mock_server.MockServerSupport;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Slf4j
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
        assertThat(githubRepositoryRepository.findAll())
                .isEmpty();
        githubSyncJobRepository
                .save(MockGithubJobEntity.createReadyJob());
    }

    @AfterEach
    void tearDown() {
        githubMockServer.tearDown();
        githubSyncJobRepository.deleteAllInBatch();
        githubRepositoryRepository.deleteAllInBatch();
    }

    @Test
    void repoSyncing_whenThereIsAnDueReadyJob_thenProcessSyncing() {
        githubSyncScheduler.repoSyncing();
        await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(githubRepositoryRepository.findAll().size()).isNotZero();
                    assertThat(
                            githubSyncJobRepository.findById(1L)
                                    .orElseThrow().getNextRunAt())
                            .isEqualTo(Instant.now(fixedClock)
                                    .plus(1, ChronoUnit.HOURS));
                });
    }

    @Test
    void repoSyncing_whenThereAreMultipleScheduler_thenOnlyOneSucceed() {
        final int threadCounts = 2;

        try (RaceConditionSimulator raceConditionSimulator = RaceConditionSimulator.getRaceConditionSimulator(threadCounts)) {
            int failureThreadCounts = 0;
            List<TaskResult<Void>> taskResults =
                    raceConditionSimulator.execute(
                            () -> {
                                githubSyncScheduler.repoSyncing();
                                return null;
                            });

            await().atMost(10, TimeUnit.SECONDS)
                    .untilAsserted(
                            () -> {
                                Map<Boolean, List<TaskResult<Void>>> partitioned =
                                        taskResults
                                                .stream()
                                                .collect(Collectors.partitioningBy(TaskResult::isSuccess));

                                assertThat(partitioned.get(Boolean.FALSE).size()).isEqualTo(threadCounts - 1);
                                assertThat(githubRepositoryRepository.findAll().size()).isNotZero();
                                assertThat(
                                        githubSyncJobRepository.findById(1L)
                                                .orElseThrow().getNextRunAt())
                                        .isEqualTo(Instant.now(fixedClock)
                                                .plus(1, ChronoUnit.HOURS));
                            }
                    );
        }

    }


}