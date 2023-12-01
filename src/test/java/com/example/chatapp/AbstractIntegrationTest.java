package com.example.chatapp;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

public class AbstractIntegrationTest {
    static final PostgreSQLContainer<?> PSQL_CONTAINER;

    static {

        PSQL_CONTAINER = new PostgreSQLContainer<>("postgres:latest")
                .withUsername("postgres")
                .withPassword("postgres")
                .withDatabaseName("postgres")
                .withExposedPorts(5432);
        PSQL_CONTAINER.start();
    }

    public static String getUrl() {
        return String.format("jdbc:postgresql://localhost:%s/%s", PSQL_CONTAINER.getFirstMappedPort(),
                PSQL_CONTAINER.getDatabaseName());
    }

    @DynamicPropertySource
    public static void dynamicPropertySource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", AbstractIntegrationTest::getUrl);
        registry.add("spring.datasource.username", PSQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", PSQL_CONTAINER::getPassword);

        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.test.database.replace", () -> "none");
        registry.add("spring.jpa.generate-ddl", () -> "true");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
        registry.add("secret.hash.salt", () -> "salt_123");
        registry.add("secret.jwt.key", () -> "secret_jwt_123");
        registry.add("spring.datasource.log-statement", () -> true);
    }
}