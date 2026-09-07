package support.mock_server;

import com.example.github_manager.repositories_sync.configuration.rest_client.GithubRestClientProperties;
import org.mockserver.client.MockServerClient;
import org.mockserver.matchers.Times;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.mockserver.MockServerContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@TestConfiguration(proxyBeanMethods = false)
@EnableConfigurationProperties(GithubRestClientProperties.class)
public class MockServerSupport {

    private static final DockerImageName MOCKSERVER_IMAGE =
            DockerImageName.parse("mockserver/mockserver")
                    .withTag(
                            "mockserver-" +
                                    MockServerClient.class
                                            .getPackage()
                                            .getImplementationVersion()
                    );

    @Bean
    public MockServerContainer mockServerContainer() {
        MockServerContainer container =
                new MockServerContainer(MOCKSERVER_IMAGE);

        container.start();

        return container;
    }

    @Bean
    DynamicPropertyRegistrar apiPropertiesRegistrar(MockServerContainer mockServerContainer) {
        return registry -> registry.add("clients.github-service.base-url", mockServerContainer::getEndpoint);
    }

    @Bean
    public MockServerClient mockServerClient(
            MockServerContainer mockServerContainer
    ) {
        return new MockServerClient(
                mockServerContainer.getHost(),
                mockServerContainer.getServerPort()
        );
    }

    @Bean
    public GithubMockServer githubMockServer(
            MockServerClient mockServerClient,
            GithubRestClientProperties properties
    ) {
        return new GithubMockServer(
                mockServerClient,
                properties
        );
    }

    public static class GithubMockServer {

        private final MockServerClient mockServerClient;
        private final GithubRestClientProperties properties;
        private final Map<Integer, String> pageMap = Map.of(
                0, "get_owned_repo_page0.json",
                1, "get_owned_repo_page1.json",
                2, "get_owned_repo_page2.json",
                3, "get_owned_repo_page3.json",
                4, "get_owned_repo_page4.json"
        );

        public GithubMockServer(
                MockServerClient mockServerClient,
                GithubRestClientProperties properties
        ) {
            this.mockServerClient = mockServerClient;
            this.properties = properties;
        }

        public void setUp() {
            mockServerClient.reset();
            pageMap.forEach((k, v) -> {
                try {
                    String pageResponse = new ClassPathResource(v)
                            .getContentAsString(StandardCharsets.UTF_8);
                    stubGithubPage(k, pageResponse);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        }

        public void tearDown() {
            mockServerClient.close();
        }

        public void stubGithubPage(
                int page,
                String responseBody
        ) {
            mockServerClient
                    .when(
                            request()
                                    .withMethod("GET")
                                    .withPath(properties.path())
                                    .withQueryStringParameter(
                                            "affiliation",
                                            properties.affiliation()
                                    )
                                    .withQueryStringParameter(
                                            "per_page",
                                            String.valueOf(
                                                    properties.pageSize()
                                            )
                                    )
                                    .withQueryStringParameter(
                                            "page",
                                            String.valueOf(page)
                                    )
                                    .withHeader(
                                            "Accept",
                                            properties.mediaType()
                                    )
                                    .withHeader(
                                            "X-GitHub-Api-Version",
                                            properties.apiVersion()
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
    }
}