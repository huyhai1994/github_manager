package support;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

@Testcontainers
public abstract class AbstractIntegrationTest {

    private static final String MYSQL_DATABASE_NAME =
            "github_manager_test";

    private static final String MYSQL_USERNAME = "test";
    private static final String MYSQL_USER_PASSWORD = "test";
    private static final String MYSQL_DOCKER_IMAGE = "mysql:8.0";

    private static final MySQLContainer mysqlDb;


    static {
        mysqlDb = new MySQLContainer(MYSQL_DOCKER_IMAGE)
                .withDatabaseName(MYSQL_DATABASE_NAME)
                .withUsername(MYSQL_USERNAME)
                .withPassword(MYSQL_USER_PASSWORD);
        mysqlDb.start();
    }

    @DynamicPropertySource
    static void registerProperties(
            DynamicPropertyRegistry registry
    ) {
        registry.add("spring.datasource.url", mysqlDb::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlDb::getUsername);
        registry.add("spring.datasource.password", mysqlDb::getPassword);
    }
}