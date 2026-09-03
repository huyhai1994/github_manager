package com.example.github_manager.repositories_sync.configuration.task_executor;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "github-sync-executor")
@Getter
@Setter
public class GithubSyncTaskExecutorProperties {
    private int corePoolSize;
    private int maximumPoolSize;
    private int queueCapacity;
    private String threadNamePrefix;
}
