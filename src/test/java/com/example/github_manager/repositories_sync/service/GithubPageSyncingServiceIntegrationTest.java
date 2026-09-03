package com.example.github_manager.repositories_sync.service;

import com.example.github_manager.repositories_sync.configuration.rest_client.GithubRestClientProperties;
import com.example.github_manager.repositories_sync.repositories.GithubRepositoryRepository;
import com.example.github_manager.repositories_sync.repositories.GithubSyncJobRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.matchers.Times;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import support.AbstractIntegrationTest;


import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
@ActiveProfiles("test")
class GithubPageSyncingServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    GithubPageSyncingService githubPageSyncingService;

    @Autowired
    GithubSyncJobRepository githubSyncJobRepository;

    @Autowired
    GithubRepositoryRepository githubRepositoryRepository;

    @Autowired
    GithubRestClientProperties githubRestClientProperties;

    private MockServerClient mockServerClient;

    private final String PAGE_0_RESPONSE = new ClassPathResource("get_owned_repo_page0.json").getContentAsString(StandardCharsets.UTF_8);

    private final String PAGE_1_RESPONSE = new ClassPathResource("get_owned_repo_page1.json").getContentAsString(StandardCharsets.UTF_8);

    private final String PAGE_2_RESPONSE = new ClassPathResource("get_owned_repo_page2.json").getContentAsString(StandardCharsets.UTF_8);

    private final String PAGE_3_RESPONSE = new ClassPathResource("get_owned_repo_page3.json").getContentAsString(StandardCharsets.UTF_8);

    private final String PAGE_4_RESPONSE = new ClassPathResource("get_owned_repo_page4.json").getContentAsString(StandardCharsets.UTF_8);

    GithubPageSyncingServiceIntegrationTest() throws IOException {
    }

    @BeforeEach
    void setUp() {
        mockServerClient = mockServerClient();
        mockServerClient.reset();
        stubGithubPage(0, PAGE_0_RESPONSE);
        stubGithubPage(1, PAGE_1_RESPONSE);
        stubGithubPage(2, PAGE_2_RESPONSE);
        stubGithubPage(3, PAGE_3_RESPONSE);
        stubGithubPage(4, PAGE_4_RESPONSE);
    }

    private void stubGithubPage(int page, String responseBody) {
        mockServerClient
                .when(
                        request()
                                .withMethod("GET")
                                .withPath(githubRestClientProperties.path())
                                .withQueryStringParameter(
                                        "affiliation",
                                        githubRestClientProperties.affiliation()
                                )
                                .withQueryStringParameter(
                                        "per_page",
                                        String.valueOf(
                                                githubRestClientProperties.pageSize()
                                        )
                                )
                                .withQueryStringParameter(
                                        "page",
                                        String.valueOf(page)
                                )
                                .withHeader(
                                        "Accept",
                                        githubRestClientProperties.mediaType()
                                )
                                .withHeader(
                                        "X-GitHub-Api-Version",
                                        githubRestClientProperties.apiVersion()
                                ),
                        Times.exactly(1)
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(
                                        "Content-Type",
                                        "application/json"
                                )
                                .withBody(responseBody)
                );
    }

    @AfterEach
    void tearDown() {
        mockServerClient.close();
        githubSyncJobRepository.deleteAllInBatch();
        githubRepositoryRepository.deleteAllInBatch();
    }


    @Test
    void syncAllPages_whenFetchAllPages_thenSaveAllToDB() {
        githubPageSyncingService.syncAllPages();
    }
}