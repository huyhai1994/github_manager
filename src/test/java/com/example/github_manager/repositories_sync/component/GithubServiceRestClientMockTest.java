package com.example.github_manager.repositories_sync.component;

import com.example.github_manager.repositories_sync.configuration.rest_client.GithubRestClientProperties;
import com.example.github_manager.repositories_sync.dto.GithubRawResponse;
import com.example.github_manager.repositories_sync.dto.GithubRepositoryResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import support.mock_server.MockServerSupport;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(GithubServiceRestClient.class)
@EnableConfigurationProperties(GithubRestClientProperties.class)
@Import(GithubServiceRestClientMockTest.MockClientConfiguration.class)
@ActiveProfiles("test")
class GithubServiceRestClientMockTest {

    private static final int PAGE = 1;

    @Autowired
    private GithubServiceRestClient gitHubServiceRestClient;

    @Autowired
    private GithubRestClientProperties properties;

    @Autowired
    private MockRestServiceServer mockServer;


    @Test
    void getOwnedRepositories_whenResponseIsSuccessful_shouldDeserializeResponse()
            throws IOException {

        stubGetOwnedRepositoriesSuccessfully();
        GithubRawResponse gitHubRawResponse = gitHubServiceRestClient.getOwnedRepositories(PAGE);
        int statusCode = gitHubRawResponse.statusCode();
        String body = gitHubRawResponse.body();

        String linkHeader = gitHubRawResponse.headers()
                .getFirst(HttpHeaders.LINK);

        assertThat(linkHeader).contains(
                "<https://api.github.com/user/repos"
        );

        List<GithubRepositoryResponse> repositories = new ObjectMapper().readValue(body, new TypeReference<>() {
        });

        assertThat(statusCode).isEqualTo(200);

        GithubRepositoryResponse repository = repositories.stream().findFirst().orElseThrow();

        assertThat(repository.id()).isEqualTo(819177606L);
        assertThat(repository.name()).isEqualTo("-customer-manage-aspect");
        assertThat(repository.fullName())
                .isEqualTo("huyhai1994/-customer-manage-aspect");

        assertThat(repository.privateRepository()).isFalse();

        assertThat(repository.visibility()).isEqualTo("public");

        assertThat(repository.htmlUrl())
                .isEqualTo("https://github.com/huyhai1994/-customer-manage-aspect"
                );

        assertThat(repository.createdAt())
                .isEqualTo(Instant.parse("2024-06-24T01:44:27Z"));

        assertThat(repository.updatedAt())
                .isEqualTo(Instant.parse("2024-06-24T01:45:20Z"));

        assertThat(repository.pushedAt())
                .isEqualTo(Instant.parse("2024-06-24T01:48:43Z"));

        assertThat(repository.owner()).isNotNull();
        assertThat(repository.owner().login()).isEqualTo("huyhai1994");

        mockServer.verify();
    }

    private void stubGetOwnedRepositoriesSuccessfully() throws IOException {
        String responseBody = new ClassPathResource("get_repo.json")
                .getContentAsString(StandardCharsets.UTF_8);

        String linkHeader = """
                <https://api.github.com/user/repos?affiliation=owner&per_page=100&page=2>; rel="next", \
                <https://api.github.com/user/repos?affiliation=owner&per_page=100&page=5>; rel="last"
                """.trim();

        mockServer.expect(
                        requestTo(startsWith(
                                properties.baseUrl()
                                        + properties.getRepositoriesPath()
                        ))
                )
                .andExpect(method(HttpMethod.GET))
                .andExpect(queryParam("affiliation", "owner"))
                .andExpect(queryParam(
                        "per_page",
                        String.valueOf(properties.pageSize())
                ))
                .andExpect(queryParam("page", String.valueOf(PAGE)))
                .andExpect(header(
                        HttpHeaders.ACCEPT,
                        "application/vnd.github+json"
                ))
                .andExpect(header(
                        "X-GitHub-Api-Version",
                        "2022-11-28"
                ))
                .andRespond(
                        withSuccess(responseBody, MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.LINK, linkHeader)
                                .header("X-RateLimit-Limit", "5000")
                                .header("X-RateLimit-Remaining", "4999")
                                .header("X-RateLimit-Reset", "1787558400")

                );
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class MockClientConfiguration {

        @Bean
        RestClient githubApiRestClient(
                RestClient.Builder builder,
                GithubRestClientProperties properties
        ) {
            return builder
                    .baseUrl(properties.baseUrl())
                    .defaultHeader(
                            HttpHeaders.ACCEPT,
                            MediaType.APPLICATION_JSON_VALUE
                    )
                    .build();
        }
    }
}