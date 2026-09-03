package com.example.github_manager.repositories_sync.configuration.task_executor;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@RequiredArgsConstructor
public class GithubSyncTaskExecutor {

    private final GithubSyncTaskExecutorProperties githubSyncTaskExecutorProperties;

    @Bean(name = "githubSyncExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(githubSyncTaskExecutorProperties.getCorePoolSize());
        executor.setMaxPoolSize(githubSyncTaskExecutorProperties.getMaximumPoolSize());
        executor.setQueueCapacity(githubSyncTaskExecutorProperties.getQueueCapacity());
        executor.setThreadNamePrefix(githubSyncTaskExecutorProperties.getThreadNamePrefix());
        executor.initialize();
        return executor;
    }


}
