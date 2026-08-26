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
import org.springframework.transaction.support.TransactionTemplate;
import support.AbstractIntegrationTest;
import support.concurency.RaceConditionSimulator;
import support.mock.MockGithubJobEntity;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
            try {
                List<Integer> results = raceConditionSimulator.execute(
                        () -> transactionTemplate.execute(status ->
                                githubSyncJobRepository.markSubmittedFromReady(id, fixedClock.instant())
                        ));

                assertThat(results).containsExactlyInAnyOrder(
                        1, 0
                );
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void markSyncingFromSubmitted_whenTwoThreadTryToUpdateSameTime_thenOnlyOneThreadSuccess() {
        GithubSyncJob persistedSyncJob = githubSyncJobRepository
                .saveAndFlush(MockGithubJobEntity.createSubmittedJob());

        Long id = persistedSyncJob.getId();

        try (RaceConditionSimulator raceConditionSimulator = RaceConditionSimulator.getRaceConditionSimulator(2)) {
            try {
                List<Integer> results = raceConditionSimulator.execute(
                        () -> transactionTemplate.execute(status ->
                                githubSyncJobRepository.markSyncingFromSubmitted(id, fixedClock.instant())
                        ));

                assertThat(results).containsExactlyInAnyOrder(
                        1, 0
                );
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void markReadyFromSyncing_whenTwoThreadTryToUpdateSameTime_thenOnlyOneThreadSuccess() {
        GithubSyncJob persistedSyncJob = githubSyncJobRepository
                .saveAndFlush(MockGithubJobEntity.createSyncingJob());

        Long id = persistedSyncJob.getId();

        try (RaceConditionSimulator raceConditionSimulator = RaceConditionSimulator.getRaceConditionSimulator(2)) {
            try {
                List<Integer> results = raceConditionSimulator.execute(
                        () -> transactionTemplate.execute(status ->
                                githubSyncJobRepository.markReadyFromSyncing(id, fixedClock.instant(), fixedClock.instant().plus(1, ChronoUnit.HOURS))
                        ));

                assertThat(results).containsExactlyInAnyOrder(
                        1, 0
                );

                GithubSyncJob updatedSyncJobs = githubSyncJobRepository
                        .findById(id).orElseThrow();
                assertThat(updatedSyncJobs
                        .getNextRunAt()).isEqualTo(fixedClock.instant().plus(1, ChronoUnit.HOURS));
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void markSyncFailedFromSyncing_whenTwoThreadTryToUpdateSameTime_thenOnlyOneThreadSuccess() {
        GithubSyncJob persistedSyncJob = githubSyncJobRepository
                .saveAndFlush(MockGithubJobEntity.createSyncingJob());

        Long id = persistedSyncJob.getId();

        try (RaceConditionSimulator raceConditionSimulator = RaceConditionSimulator.getRaceConditionSimulator(2)) {
            try {
                List<Integer> results = raceConditionSimulator.execute(
                        () -> transactionTemplate.execute(status ->
                                githubSyncJobRepository.markSyncFailedFromSyncing(id, fixedClock.instant())
                        ));

                assertThat(results).containsExactlyInAnyOrder(
                        1, 0
                );

                GithubSyncJob updatedSyncJobs = githubSyncJobRepository
                        .findById(id).orElseThrow();
                assertThat(updatedSyncJobs.getNextRunAt()).isNull();
                assertThat(updatedSyncJobs.getFailedAt()).isNotNull();

            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        }
    }
}