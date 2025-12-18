package com.hongsy.ecommrsapi.product.repository;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "jwt.secret=v6yB4E9b8f2G7hN0m1p3Q5s8u9vA2d4fG6hJ8kL0m2n4p6q8r0t2v4x6z8A1B3C",
        "JWT_SECRET_KEY=v6yB4E9b8f2G7hN0m1p3Q5s8u9vA2d4fG6hJ8kL0m2n4p6q8r0t2v4x6z8A1B3C"
    })
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    // static으로 선언하여 전체 테스트 세션에서 컨테이너 공유
    @Container
    protected static final PostgreSQLContainer<?> postgresContainer =
        new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }
}
