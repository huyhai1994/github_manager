package support;

import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.ToxiproxyClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.toxiproxy.ToxiproxyContainer;

import java.io.IOException;
import java.util.stream.Stream;

@Testcontainers
public abstract class AbstractIntegrationTest {

    private static final String MYSQL_DATABASE_NAME =
            "github_manager_test";

    private static final int MYSQL_PROXY_PORT = 8666;

    private static final Network NETWORK =
            Network.newNetwork();

    private static final MySQLContainer mysqlDb;


    private static final ToxiproxyContainer toxiproxyContainer;

    protected static Proxy mysqlProxy;

    static {
        mysqlDb = new MySQLContainer("mysql:8.0")
                .withDatabaseName(MYSQL_DATABASE_NAME)
                .withUsername("test")
                .withPassword("test")
                .withNetwork(NETWORK)
                .withNetworkAliases("mysql");


        toxiproxyContainer = new ToxiproxyContainer(
                "ghcr.io/shopify/toxiproxy:2.5.0"
        ).withNetwork(NETWORK);

        Startables.deepStart(
                Stream.of(
                        mysqlDb,
                        toxiproxyContainer
                )
        ).join();
        createProxies();
    }

    private static void createProxies() {
        ToxiproxyClient toxiproxyClient = new ToxiproxyClient(
                toxiproxyContainer.getHost(),
                toxiproxyContainer.getControlPort()
        );

        try {
            mysqlProxy = toxiproxyClient.createProxy(
                    "mysql",
                    "0.0.0.0:" + MYSQL_PROXY_PORT,
                    "mysql:3306"
            );
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not create Toxiproxy proxies",
                    exception
            );
        }
    }

    @DynamicPropertySource
    static void registerProperties(
            DynamicPropertyRegistry registry
    ) {
        registry.add(
                "spring.datasource.username",
                mysqlDb::getUsername
        );

        registry.add(
                "spring.datasource.password",
                mysqlDb::getPassword
        );

        registry.add(
                "spring.datasource.url",
                () -> String.format(
                        "jdbc:mysql://%s:%d/%s"
                                + "?connectTimeout=%d"
                                + "&socketTimeout=%d"
                                + "&tcpKeepAlive=%b",
                        toxiproxyContainer.getHost(),
                        toxiproxyContainer.getMappedPort(
                                MYSQL_PROXY_PORT
                        ),
                        MYSQL_DATABASE_NAME,
                        5_000,
                        5_000,
                        false
                )
        );
    }
}