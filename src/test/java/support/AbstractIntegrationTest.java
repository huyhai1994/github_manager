package support;

import org.mockserver.client.MockServerClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.mockserver.MockServerContainer;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class AbstractIntegrationTest {

    private static final DockerImageName MYSQL_IMAGE =
            DockerImageName.parse("mysql:8.0.43");

    private static final DockerImageName MOCKSERVER_IMAGE =
            DockerImageName.parse("mockserver/mockserver")
                    .withTag(
                            "mockserver-" +
                                    MockServerClient.class
                                            .getPackage()
                                            .getImplementationVersion()
                    );

    protected static final MySQLContainer MYSQL =
            new MySQLContainer(MYSQL_IMAGE)
                    .withDatabaseName("github_manager_test")
                    .withUsername("test")
                    .withPassword("test");

    protected static final MockServerContainer MOCK_SERVER =
            new MockServerContainer(MOCKSERVER_IMAGE);

    static {
        MYSQL.start();
        MOCK_SERVER.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);

        registry.add(
                "clients.github-service.base-url",
                MOCK_SERVER::getEndpoint
        );
    }

    protected MockServerClient mockServerClient() {
        return new MockServerClient(
                MOCK_SERVER.getHost(),
                MOCK_SERVER.getServerPort()
        );
    }
}