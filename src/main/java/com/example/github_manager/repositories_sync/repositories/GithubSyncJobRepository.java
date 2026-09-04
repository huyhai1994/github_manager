package com.example.github_manager.repositories_sync.repositories;


import com.example.github_manager.repositories_sync.entity.GithubSyncJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface GithubSyncJobRepository extends JpaRepository<GithubSyncJob, Long> {

    @Modifying
    @Query("""
            update
                    GithubSyncJob  j
            set
                    j.status = GithubSyncJobStatus.SUBMITTED,
                    j.updatedAt = :now,
                    j.submittedAt = :now
            where
                    j.id = :id
            and
                    j.status = GithubSyncJobStatus.READY
            """)
    int markSubmittedFromReady(
            @Param("id") Long id,
            @Param("now") Instant now
    );

    @Modifying
    @Query("""
            update
                    GithubSyncJob  j
            set
                    j.status = GithubSyncJobStatus.SYNCING,
                    j.updatedAt = :now,
                    j.syncedAt = :now
            where
                    j.id = :id
            and
                    j.status = GithubSyncJobStatus.SUBMITTED
            """)
    int markSyncingFromSubmitted(
            @Param("id") Long id,
            @Param("now") Instant now
    );

    @Modifying
    @Query("""
            update
                    GithubSyncJob  j
            set
                    j.status = GithubSyncJobStatus.READY,
                    j.updatedAt = :now,
                    j.nextRunAt = :nextRunAt
            where
                    j.id = :id
            and
                    j.status = GithubSyncJobStatus.SYNCING
            """)
    int markReadyFromSyncing(
            @Param("id") Long id,
            @Param("now") Instant now,
            @Param("nextRunAt") Instant nextRunAt
    );

    @Modifying
    @Query("""
            update
                    GithubSyncJob  j
            set
                    j.status = GithubSyncJobStatus.SYNCING_FAILED,
                    j.updatedAt = :now,
                    j.failedAt = :now,
                    j.nextRunAt = null
            where
                    j.id = :id
            and
                    j.status = GithubSyncJobStatus.SYNCING
            """)
    int markSyncFailedFromSyncing(
            @Param("id") Long id,
            @Param("now") Instant now
    );

    @Query("""
            select sj.id
            from GithubSyncJob sj 
            where  sj.status = GithubSyncJobStatus.READY
              and sj.nextRunAt <= :now
              limit 1
            """)
    Optional<Long> findDueReadyJob(
            @Param("now") Instant now
    );

}
