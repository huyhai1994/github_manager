package com.example.github_manager.repositories_sync.repositories;

import com.example.github_manager.repositories_sync.entity.GithubSyncJob;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import support.AbstractIntegrationTest;
import support.concurency.RaceConditionSimulator;
import support.concurency.TaskResult;
import support.mock.MockGithubJobEntity;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class GithubSyncJobRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    GithubSyncJobRepository githubSyncJobRepository;

    @Autowired
    TransactionTemplate transactionTemplate;

    @Autowired
    EntityManager entityManager;

    Clock fixedClock = Clock.fixed(
            Instant.parse("2026-08-26T00:00:00Z"),
            ZoneOffset.UTC
    );

    @BeforeEach
    void setup() {
        githubSyncJobRepository.deleteAllInBatch();
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void markSubmittedFromReady_whenTwoThreadTryToUpdateSameTime_thenOnlyOneThreadSuccess() {
        GithubSyncJob persistedSyncJob = githubSyncJobRepository.saveAndFlush(MockGithubJobEntity.createReadyJob());

        Long id = persistedSyncJob.getId();

        try (RaceConditionSimulator raceConditionSimulator = RaceConditionSimulator.getRaceConditionSimulator(2)) {
            List<Integer> results = raceConditionSimulator.execute(
                            () -> transactionTemplate.execute(status ->
                                    githubSyncJobRepository.markSubmittedFromReady(id, fixedClock.instant())
                            )).stream()
                    .map(TaskResult::result)
                    .toList();

            assertThat(results).containsExactlyInAnyOrder(
                    1, 0
            );
        }
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void markSyncingFromSubmitted_whenTwoThreadTryToUpdateSameTime_thenOnlyOneThreadSuccess() {
        GithubSyncJob persistedSyncJob = githubSyncJobRepository
                .saveAndFlush(MockGithubJobEntity.createSubmittedJob());

        Long id = persistedSyncJob.getId();

        try (RaceConditionSimulator raceConditionSimulator = RaceConditionSimulator.getRaceConditionSimulator(2)) {
            var results = raceConditionSimulator.execute(
                            () -> transactionTemplate.execute(status ->
                                    githubSyncJobRepository.markSyncingFromSubmitted(id, fixedClock.instant())
                            ))
                    .stream()
                    .map(TaskResult::result)
                    .toList();

            assertThat(results).containsExactlyInAnyOrder(
                    1, 0
            );
        }
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void markReadyFromSyncing_whenTwoThreadTryToUpdateSameTime_thenOnlyOneThreadSuccess() {
        GithubSyncJob persistedSyncJob = githubSyncJobRepository
                .saveAndFlush(MockGithubJobEntity.createSyncingJob());

        Long id = persistedSyncJob.getId();

        try (RaceConditionSimulator raceConditionSimulator = RaceConditionSimulator.getRaceConditionSimulator(2)) {

            TransactionCallback<Integer> callback =
                    status ->
                            githubSyncJobRepository.markReadyFromSyncing(id, fixedClock.instant(), fixedClock.instant().plus(1, ChronoUnit.HOURS));

            Callable<Integer> integerCallable = () -> transactionTemplate.execute(callback);
            var results = raceConditionSimulator.execute(integerCallable)
                    .stream()
                    .map(TaskResult::result)
                    .toList();

            assertThat(results).containsExactlyInAnyOrder(
                    1, 0
            );

            GithubSyncJob updatedSyncJobs = githubSyncJobRepository
                    .findById(id).orElseThrow();
            assertThat(updatedSyncJobs
                    .getNextRunAt()).isEqualTo(fixedClock.instant().plus(1, ChronoUnit.HOURS));
        }
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void markSyncFailedFromSyncing_whenTwoThreadTryToUpdateSameTime_thenOnlyOneThreadSuccess() {
        GithubSyncJob persistedSyncJob = githubSyncJobRepository
                .saveAndFlush(MockGithubJobEntity.createSyncingJob());

        Long id = persistedSyncJob.getId();

        try (RaceConditionSimulator raceConditionSimulator = RaceConditionSimulator.getRaceConditionSimulator(2)) {
            List<Integer> results = raceConditionSimulator.execute(
                            () -> transactionTemplate.execute(status ->
                                    githubSyncJobRepository.markSyncFailedFromSyncing(id, fixedClock.instant())
                            ))
                    .stream()
                    .map(TaskResult::result)
                    .toList();

            assertThat(results).containsExactlyInAnyOrder(
                    1, 0
            );

            GithubSyncJob updatedSyncJobs = githubSyncJobRepository
                    .findById(id).orElseThrow();
            assertThat(updatedSyncJobs.getNextRunAt()).isNull();
            assertThat(updatedSyncJobs.getFailedAt()).isNotNull();

        }
    }

    @Test
    void findDueReadyJob_returnIdWhenThereIsDueReadyJob() {
        GithubSyncJob dueReadJob = githubSyncJobRepository
                .saveAndFlush(MockGithubJobEntity.createReadyJob());
        GithubSyncJob failedJob = githubSyncJobRepository
                .saveAndFlush(MockGithubJobEntity.createFailedJob());
        entityManager.flush();
        entityManager.clear();
        Instant now = Instant.parse("2026-08-25T03:00:00Z");
        Long foundJobId = githubSyncJobRepository.findDueReadyJob(now).orElseThrow();
        assertThat(foundJobId).isNotNull();
        assertThat(foundJobId).isEqualTo(dueReadJob.getId());
    }
}