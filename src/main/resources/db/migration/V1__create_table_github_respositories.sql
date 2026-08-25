CREATE TABLE IF NOT EXISTS `github_repositories`
(
    `id`                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `github_id`         BIGINT          NOT NULL UNIQUE,
    `owner_login`       VARCHAR(255)    NOT NULL,
    `full_name`         VARCHAR(512)    NOT NULL,
    `html_url`          VARCHAR(2048)   NOT NULL,
    `github_updated_at` DATETIME        NOT NULL,
    `github_created_at` DATETIME        NOT NULL,
    `github_pushed_at`  DATETIME        NOT NULL,
    `status`            VARCHAR(30)     NOT NULL,
    `created_at`        DATETIME        NOT NULL,
    `updated_at`        DATETIME        NOT NULL,
    `private`           TINYINT         NOT NULL,
    `version`           BIGINT          NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
);


