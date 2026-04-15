package com.clickhouse.alnscodingexercise.domains.shared.web.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.client.model.ClientCreateStoreResponse;
import dev.openfga.sdk.api.client.model.ClientWriteAuthorizationModelResponse;
import dev.openfga.sdk.api.configuration.ClientConfiguration;
import dev.openfga.sdk.api.model.CreateStoreRequest;
import dev.openfga.sdk.api.model.WriteAuthorizationModelRequest;
import io.restassured.RestAssured;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.openfga.OpenFGAContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractControllerIntegrationTest {

    private static String openFgaStoreId;
    private static String openFgaAuthorizationModelId;

    protected static final ObjectMapper STATIC_OBJECT_MAPPER = new ObjectMapper();

    @LocalServerPort
    private int port;

    @Autowired
    protected ResourceLoader resourceLoader;

    @Container
    public static PostgreSQLContainer<?> postgreSqlContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16.3"));

    @Container
    public static OpenFGAContainer openFgaContainer = new OpenFGAContainer(DockerImageName.parse("openfga/openfga:v1.13.1"))
            .withExposedPorts(8080);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) throws InterruptedException {
        startContainers();
        initializeOpenFgaStoreAndModel();

        // PostgreSQL properties
        registry.add("spring.datasource.url", postgreSqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSqlContainer::getUsername);
        registry.add("spring.datasource.password", postgreSqlContainer::getPassword);
        registry.add("spring.docker.compose.enabled", () -> false);

        // OpenFGA properties
        registry.add("openfga.fga-api-url", () -> String.format("http://%s:%d", openFgaContainer.getHost(), openFgaContainer.getMappedPort(8080)));
        registry.add("openfga.fga-store-id", () -> openFgaStoreId);
        registry.add("openfga.fga-authorization-model-id", () -> openFgaAuthorizationModelId);
        registry.add("openfga.fga-should-import-initial-structure", () -> false);
    }

    private static void startContainers() throws InterruptedException {

        // Network commonNetwork = Network.newNetwork();

        postgreSqlContainer.addEnv("POSTGRES_DB", "test");
        postgreSqlContainer.addEnv("POSTGRES_PORT", "5432");
        postgreSqlContainer.addEnv("POSTGRES_USER", "test");
        postgreSqlContainer.addEnv("POSTGRES_PASSWORD", "test");
        postgreSqlContainer.withAccessToHost(true);
        // postgreSqlContainer.setNetwork(commonNetwork);
        postgreSqlContainer.setNetworkAliases(List.of("dbPostgresTestHost"));
        postgreSqlContainer.withInitScripts(
                "docker-ninja/postgresql/init-configs/init-scripts/01-create-roles-users-dbs-schemas.sql"
                , "docker-ninja/postgresql/init-configs/init-scripts/02-openfga-db-dump-to-load-01.sql"
                , "docker-ninja/postgresql/init-configs/init-scripts/10-dbs-clickhouse-apps-dump-to-load.sql"
        );

        postgreSqlContainer.withReuse(true);
        postgreSqlContainer.withLogConsumer(System.out::println);
        postgreSqlContainer.start();

        // openFgaContainer.setNetwork(commonNetwork);
        openFgaContainer.setNetworkAliases(List.of("svcOpenFGAHost"));
        openFgaContainer.withAccessToHost(true);
        // openFgaContainer.withLogConsumer(System.out::println);
        openFgaContainer.dependsOn(postgreSqlContainer);
        /*
        openFgaContainer.addEnv("OPENFGA_DATASTORE_ENGINE", "postgres");
        openFgaContainer.addEnv("OPENFGA_DATASTORE_URI", String.format("postgres://%s:%s@%s:%d/%s?sslmode=disable",  // &search_path=%s",
                "test",
                "test",
                // "dbPostgresTestHost",
                StringUtils.remove(postgreSqlContainer.getContainerName(), "/"),
                postgreSqlContainer.getMappedPort(5432),
                "test"
                // "schm01"
                )
        );
        */

        openFgaContainer.addEnv("OPENFGA_DATASTORE_MAX_OPEN_CONNS", "100");
        openFgaContainer.addEnv("OPENFGA_LOG_LEVEL", "debug");

        openFgaContainer.start();

        // Startables.deepStart(Stream.of(postgreSqlContainer, openFgaContainer)).join();
    }

    private static void initializeOpenFgaStoreAndModel() {
        if (openFgaStoreId != null && openFgaAuthorizationModelId != null) {
            return;
        }

        String fgaApiUrl = String.format("http://%s:%d", openFgaContainer.getHost(), openFgaContainer.getMappedPort(8080));

        try {
            OpenFgaClient client = new OpenFgaClient(new ClientConfiguration().apiUrl(fgaApiUrl));

            ClientCreateStoreResponse createStoreResponse = client.createStore(
                    new CreateStoreRequest().name("it-store-" + UUID.randomUUID())
            ).get();
            openFgaStoreId = createStoreResponse.getId();
            client.setStoreId(openFgaStoreId);

            byte[] modelBytes = new ClassPathResource(
                    "openfga/initial-model-tuples-data/domain-assetmgmt-resourcething-model-schema.json"
            ).getInputStream().readAllBytes();
            WriteAuthorizationModelRequest modelRequest = STATIC_OBJECT_MAPPER.readValue(modelBytes, WriteAuthorizationModelRequest.class);

            ClientWriteAuthorizationModelResponse modelResponse = client.writeAuthorizationModel(modelRequest).get();
            openFgaAuthorizationModelId = modelResponse.getAuthorizationModelId();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize OpenFGA store/model for integration tests", e);
        }
    }

    public void setUpRestAssuredPort() {
        RestAssured.port = port;
    }

}
