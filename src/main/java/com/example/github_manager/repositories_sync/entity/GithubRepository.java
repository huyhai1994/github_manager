package com.example.github_manager.repositories_sync.entity;

import com.example.github_manager.repositories_sync.dto.RepositoriesState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "github_repositories")
public class GithubRepository {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "github_id", nullable = false, unique = true)
    private Long githubId;

    @Column(name = "owner_login", nullable = false)
    private String ownerLogin;

    @Column(name = "full_name", length = 512, nullable = false)
    private String fullName;

    @Column(name = "html_url", length = 2048, nullable = false)
    private String htmlUrl;

    @Column(name = "github_updated_at", nullable = false)
    private LocalDateTime githubUpdatedAt;

    @Column(name = "github_created_at", nullable = false)
    private LocalDateTime githubCreatedAt;

    @Column(name = "github_pushed_at", nullable = false)
    private LocalDateTime githubPushedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private RepositoriesState status;

    @Version
    private Long version;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "private", columnDefinition = "TINYINT(1)", nullable = false)
    private Boolean isPrivate = Boolean.TRUE;

}
