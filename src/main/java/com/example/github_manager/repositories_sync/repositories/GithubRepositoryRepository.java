package com.example.github_manager.repositories_sync.repositories;

import com.example.github_manager.repositories_sync.entity.GithubRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GithubRepositoryRepository extends JpaRepository<GithubRepository, Long> {
}
