package com.example.demo.repository;

import com.example.demo.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTestEmbedded {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldSaveUser() {
        User user = new User(null, "test user", "secret password", "user@email.com", Instant.now(), true);
        User savedUser = userRepository.save(user);

        Assertions.assertThat(savedUser).usingRecursiveComparison().ignoringFields("userId").isEqualTo(user);
    }

    @Test
    @Sql("classpath:test-data.sql")
    public void shouldSaveUsersThroughSqlFile() {

        Optional<User> test = userRepository.findByUsername("testuser_sql");
        Assertions.assertThat(test).isNotEmpty();

    }

}