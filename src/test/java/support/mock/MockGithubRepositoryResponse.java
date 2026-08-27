package support.mock;

import com.example.github_manager.repositories_sync.dto.GithubOwnerResponse;
import com.example.github_manager.repositories_sync.dto.GithubRepositoryResponse;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MockGithubRepositoryResponse {
    private MockGithubRepositoryResponse() {
    }

    public static final GithubOwnerResponse OWNER =
            new GithubOwnerResponse(
                    "nguyenhuyhai",
                    "https://github.com/nguyenhuyhai"
            );

    public static final GithubRepositoryResponse REPOSITORY_1 =
            new GithubRepositoryResponse(
                    1_001L,
                    "github-manager",
                    "nguyenhuyhai/github-manager",
                    OWNER,
                    "https://github.com/nguyenhuyhai/github-manager",
                    "public",
                    Instant.parse("2026-08-01T02:00:00Z"),
                    Instant.parse("2026-08-25T08:30:00Z"),
                    Instant.parse("2026-08-25T08:20:00Z"),
                    false
            );

    public static final GithubRepositoryResponse REPOSITORY_2 =
            new GithubRepositoryResponse(
                    1_002L,
                    "file-upload-service",
                    "nguyenhuyhai/file-upload-service",
                    OWNER,
                    "https://github.com/nguyenhuyhai/file-upload-service",
                    "private",
                    Instant.parse("2026-07-01T03:00:00Z"),
                    Instant.parse("2026-08-20T09:15:00Z"),
                    Instant.parse("2026-08-20T09:10:00Z"),
                    true
            );

    public static final GithubRepositoryResponse REPOSITORY_3 =
            new GithubRepositoryResponse(
                    1_003L,
                    "notification-service",
                    "nguyenhuyhai/notification-service",
                    OWNER,
                    "https://github.com/nguyenhuyhai/notification-service",
                    "public",
                    Instant.parse("2026-08-10T01:30:00Z"),
                    Instant.parse("2026-08-22T10:45:00Z"),
                    Instant.parse("2026-08-22T10:40:00Z"),
                    false
            );

    public static final GithubRepositoryResponse REPOSITORY_4 =
            new GithubRepositoryResponse(
                    1_004L,
                    "personal-cloud-sync",
                    "nguyenhuyhai/personal-cloud-sync",
                    OWNER,
                    "https://github.com/nguyenhuyhai/personal-cloud-sync",
                    "private",
                    Instant.parse("2026-06-01T04:00:00Z"),
                    Instant.parse("2026-08-15T07:20:00Z"),
                    Instant.parse("2026-08-15T07:15:00Z"),
                    true
            );


    public static List<GithubRepositoryResponse> repositories() {
        return List.of(
                REPOSITORY_1,
                REPOSITORY_2,
                REPOSITORY_3,
                REPOSITORY_4
        );
    }

    public static List<GithubRepositoryResponse> emptyRepositories() {
        return List.of();
    }
}
