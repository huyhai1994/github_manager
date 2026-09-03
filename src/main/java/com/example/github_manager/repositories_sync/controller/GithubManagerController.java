package com.example.github_manager.repositories_sync.controller;

import com.example.github_manager.repositories_sync.service.GithubManagerService;
import com.example.github_manager.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/repositories")
public class GithubManagerController {
    private final GithubManagerService githubManagerService;

    @PostMapping("/sync")
    public ResponseEntity<ApiResponse<Long>> createSyncJob() {
        Long id = githubManagerService.createSyncJob();
        return ResponseEntity.accepted()
                .body(ApiResponse.success(id));
    }

}
