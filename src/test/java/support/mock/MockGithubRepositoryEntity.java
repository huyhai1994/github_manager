package support.mock;

import com.example.github_manager.repositories_sync.dto.RepositoriesState;
import com.example.github_manager.repositories_sync.entity.GithubRepository;

import java.time.Instant;

public final class MockGithubRepositoryEntity {

    private MockGithubRepositoryEntity() {
    }

    public static final class Entity2 {

        public static final Long GITHUB_ID = 200000000L;
        public static final String OWNER_LOGIN = "test_owner2";
        public static final String NAME = "github-manager2";
        public static final String FULL_NAME =
                OWNER_LOGIN + "/" + NAME;
        public static final String HTML_URL =
                "https://github.com/" + FULL_NAME;

        public static final Instant GITHUB_CREATED_AT =
                Instant.parse("2026-08-01T10:00:00Z");

        public static final Instant GITHUB_UPDATED_AT =
                Instant.parse("2026-08-24T15:30:00Z");

        public static final Instant GITHUB_PUSHED_AT =
                Instant.parse("2026-08-24T15:25:00Z");
        public static final RepositoriesState STATUS =
                RepositoriesState.SYNCING;

        public static final boolean IS_PRIVATE = true;

        private Entity2() {
        }

        public static GithubRepository create() {
            GithubRepository entity = new GithubRepository();

            entity.setGithubId(GITHUB_ID);
            entity.setOwnerLogin(OWNER_LOGIN);
            entity.setFullName(FULL_NAME);
            entity.setHtmlUrl(HTML_URL);
            entity.setGithubCreatedAt(GITHUB_CREATED_AT);
            entity.setGithubUpdatedAt(GITHUB_UPDATED_AT);
            entity.setGithubPushedAt(GITHUB_PUSHED_AT);
            entity.setStatus(STATUS);
            entity.setIsPrivate(IS_PRIVATE);

            return entity;
        }

    }

    public static final class Entity1 {

        public static final Long GITHUB_ID = 100000000L;
        public static final String OWNER_LOGIN = "test_owner1";
        public static final String NAME = "github-manager1";
        public static final String FULL_NAME =
                OWNER_LOGIN + "/" + NAME;
        public static final String HTML_URL =
                "https://github.com/" + FULL_NAME;

        public static final Instant GITHUB_CREATED_AT =
                Instant.parse("2026-08-01T10:00:00Z");

        public static final Instant GITHUB_UPDATED_AT =
                Instant.parse("2026-08-24T15:30:00Z");

        public static final Instant GITHUB_PUSHED_AT =
                Instant.parse("2026-08-24T15:25:00Z");
        public static final RepositoriesState STATUS =
                RepositoriesState.SYNCING;

        public static final boolean IS_PRIVATE = true;

        private Entity1() {
        }

        public static GithubRepository create() {
            GithubRepository entity = new GithubRepository();

            entity.setGithubId(GITHUB_ID);
            entity.setOwnerLogin(OWNER_LOGIN);
            entity.setFullName(FULL_NAME);
            entity.setHtmlUrl(HTML_URL);
            entity.setGithubCreatedAt(GITHUB_CREATED_AT);
            entity.setGithubUpdatedAt(GITHUB_UPDATED_AT);
            entity.setGithubPushedAt(GITHUB_PUSHED_AT);
            entity.setStatus(STATUS);
            entity.setIsPrivate(IS_PRIVATE);

            return entity;
        }

    }
}