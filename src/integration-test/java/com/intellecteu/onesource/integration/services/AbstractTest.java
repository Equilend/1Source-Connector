package com.intellecteu.onesource.integration.services;


import com.intellecteu.onesource.integration.TestApplication;
import com.intellecteu.onesource.integration.repository.TradeEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Parent test class for integration tests. Uses testcontainers for integration purposes and launches with "it"
 * profile.
 *
 * A new integration test should be extended from this class.
 */
@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("it")
public class AbstractTest {

    @Autowired
    TradeEventRepository tradeEventRepository;

    private static final String POSTGRES_IMAGE = "postgres:15-alpine";

    public static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>(POSTGRES_IMAGE)
            .withInitScript("db/migration/V1__initial_db.sql");

    @DynamicPropertySource
    public static void configurePostgres(DynamicPropertyRegistry registry) {
        postgres.start();
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}
