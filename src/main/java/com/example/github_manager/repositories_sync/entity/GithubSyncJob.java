package com.example.github_manager.repositories_sync.entity;

import com.example.github_manager.repositories_sync.dto.GithubSyncJobStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "github_sync_job")
@Getter
@Setter
public class GithubSyncJob {
    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private GithubSyncJobStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "synced_at")
    private Instant syncedAt;

    @Column(name = "next_run_at")
    private Instant nextRunAt;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "sync_failed_at")
    private Instant failedAt;


}
