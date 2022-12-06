package com.example.demo.repository;

import com.example.demo.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;

@DataJpaTest
//@Testcontainers  {commented due to performance improvement}
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest extends BaseTest {

//    {commented due to performance improvement}
//    @Container
//    PostgreSQLContainer container = (PostgreSQLContainer) new PostgreSQLContainer<>("postgres:latest")
//            .withDatabaseName("spring-reddit-test-db")
//            .withUsername("testuser")
//            .withPassword("pass");

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldSaveUser() {
        User expectedUserObject = new User(null, "test user", "secret password", "user@email.com", Instant.now(), true);
        User actualUserObject = userRepository.save(expectedUserObject);

        Assertions.assertThat(actualUserObject).usingRecursiveComparison().ignoringFields("userId").isEqualTo(expectedUserObject);
    }

}