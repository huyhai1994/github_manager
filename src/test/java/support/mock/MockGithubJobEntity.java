package support.mock;

import com.example.github_manager.repositories_sync.dto.GithubSyncJobStatus;
import com.example.github_manager.repositories_sync.entity.GithubSyncJob;

import java.time.Instant;

public final class MockGithubJobEntity {

    private MockGithubJobEntity() {
    }

    public static GithubSyncJob createReadyJob() {
        GithubSyncJob entity = new GithubSyncJob();

        entity.setId(1L);
        entity.setStatus(GithubSyncJobStatus.READY);
        entity.setCreatedAt(Instant.parse("2026-08-25T01:00:00Z"));
        entity.setUpdatedAt(Instant.parse("2026-08-25T01:00:00Z"));
        entity.setSyncedAt(null);
        entity.setNextRunAt(Instant.parse("2026-08-25T02:00:00Z"));
        entity.setSubmittedAt(Instant.parse("2026-08-25T00:59:00Z"));
        entity.setFailedAt(null);

        return entity;
    }

    public static GithubSyncJob createSubmittedJob() {
        GithubSyncJob entity = new GithubSyncJob();

        entity.setId(2L);
        entity.setStatus(GithubSyncJobStatus.SUBMITTED);
        entity.setCreatedAt(Instant.parse("2026-08-25T03:00:00Z"));
        entity.setUpdatedAt(Instant.parse("2026-08-25T03:10:00Z"));
        entity.setSyncedAt(null);
        entity.setNextRunAt(null);
        entity.setSubmittedAt(Instant.parse("2026-08-25T03:01:00Z"));
        entity.setFailedAt(null);

        return entity;
    }

    public static GithubSyncJob createSyncingJob() {
        GithubSyncJob entity = new GithubSyncJob();

        entity.setId(2L);
        entity.setStatus(GithubSyncJobStatus.SYNCING);
        entity.setCreatedAt(Instant.parse("2026-08-25T03:00:00Z"));
        entity.setSubmittedAt(Instant.parse("2026-08-25T03:01:00Z"));
        entity.setSyncedAt(Instant.parse("2026-08-23T03:10:10Z"));
        entity.setUpdatedAt(Instant.parse("2026-08-25T03:10:10Z"));
        entity.setNextRunAt(null);
        entity.setFailedAt(null);

        return entity;
    }
}