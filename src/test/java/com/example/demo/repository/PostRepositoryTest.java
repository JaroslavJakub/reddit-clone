package com.example.demo.repository;

import com.example.demo.model.Post;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;

@DataJpaTest
//@Testcontainers {commented due to performance improvement}
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostRepositoryTest extends BaseTest {

//    {commented due to performance improvement}
//    @Container
//    PostgreSQLContainer container = (PostgreSQLContainer) new PostgreSQLContainer<>("postgres:latest")
//            .withDatabaseName("spring-reddit-test-db")
//            .withUsername("testuser")
//            .withPassword("pass");

    @Autowired
    PostRepository postRepository;

    @Test
    public void shouldSavePost() {
        Post expectedPostObject = new Post(null, "First Post", "http://url.site", "Test",
                0, null, Instant.now(), null);
        Post actualPostObject = postRepository.save(expectedPostObject);
        Assertions.assertThat(actualPostObject).usingRecursiveComparison().ignoringFields("postId").isEqualTo(expectedPostObject);
    }
}