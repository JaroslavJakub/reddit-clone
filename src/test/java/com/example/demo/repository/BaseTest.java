package com.example.demo.repository;

import org.testcontainers.containers.PostgreSQLContainer;

public abstract class BaseTest {

    static PostgreSQLContainer container = (PostgreSQLContainer) new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("spring-reddit-test-db")
            .withUsername("testuser")
            .withPassword("pass");

    static {
        container.start();
    }
}
