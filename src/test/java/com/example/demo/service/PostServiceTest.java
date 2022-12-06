package com.example.demo.service;

import com.example.demo.dto.PostRequest;
import com.example.demo.dto.PostResponse;
import com.example.demo.exception.PostNotFoundException;
import com.example.demo.exception.SubredditNotFoundException;
import com.example.demo.mapper.PostMapper;
import com.example.demo.model.Post;
import com.example.demo.model.Subreddit;
import com.example.demo.model.User;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.SubredditRepository;
import com.example.demo.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private SubredditRepository subredditRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthService authService;
    @Mock
    private PostMapper postMapper;
    @Captor
    private ArgumentCaptor<Post> postArgumentCaptor;
    @InjectMocks
    private PostService postService;

//    @BeforeEach
//    public void setup() {
//        postService = new PostService(postRepository, subredditRepository, userRepository, authService, postMapper);
//    }

    //TESTS related to method - save()
    @Test
    @DisplayName("Should save post")
    @MockitoSettings(strictness = Strictness.STRICT_STUBS)
    void shouldSavePost() {
        User currentUser = new User(123L, "test user", "secret password", "user@email.com", Instant.now(), true);
        Subreddit subreddit = new Subreddit(123L, "First Subreddit", "Subreddit Description", Collections.emptyList(), Instant.now(), currentUser);
        PostRequest postRequest = new PostRequest(null, "First Subreddit", "First Post", "http://url.site", "Test");

        Post post = new Post(123L, "First Post", "http://url.site", "Test", 0, null, Instant.now(), null);

        Mockito.when(subredditRepository.findByName("First Subreddit")).thenReturn(Optional.of(subreddit));
        Mockito.when(authService.getCurrentUser()).thenReturn(currentUser);
        Mockito.when(postMapper.map(postRequest, subreddit, currentUser)).thenReturn(post);

        postService.save(postRequest);
        Mockito.verify(postRepository, Mockito.times(1)).save(postArgumentCaptor.capture());

        Assertions.assertThat(postArgumentCaptor.getValue().getPostId()).isEqualTo(123L);
        Assertions.assertThat(postArgumentCaptor.getValue().getPostName()).isEqualTo("First Post");
    }

    @Test
    @DisplayName("Should throw exception when trying to post to subreddit that does not exist")
    void shouldThrowExceptionWhenSubredditDoesNotExist() {
        PostRequest postRequest = new PostRequest();
        postRequest.setSubredditName("test");
        Mockito.when(subredditRepository.findByName("test")).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> {
            postService.save(postRequest);
        }).isInstanceOf(SubredditNotFoundException.class);
    }

    //TESTS related to method - getPostById()
    @Test
    @DisplayName("Should retrieve post by ID")
    void shouldFindPostById() {
        Post post = new Post(
                123L,
                "First Post",
                "http://url.site",
                "Test",
                0,
                null,
                Instant.now(),
                null);

        PostResponse expectedPostResponse = new PostResponse(
                123L,
                "First Post",
                "http://url.site",
                "Test",
                "Test User",
                "Test Subreddit",
                0,
                0,
                "1 Hour Ago",
                false,
                false);

        Mockito.when(postRepository.findById(123L)).thenReturn(Optional.of(post));
        Mockito.when(postMapper.mapToDto(Mockito.any(Post.class))).thenReturn(expectedPostResponse);

        PostResponse actualPostResponse = postService.getPostById(123L);

//        Assertions.assertThat(actualPostResponse.getId()).isEqualTo(expectedPostResponse.getId());
//        Assertions.assertThat(actualPostResponse.getPostName()).isEqualTo(expectedPostResponse.getPostName());
        Assertions.assertThat(actualPostResponse).usingRecursiveComparison().isEqualTo(expectedPostResponse);
    }

    @Test
    @DisplayName("Should throw exception when post with provided ID does not exist")
    void shouldThrowExceptionWhenPostDoesNotExist() {
        Mockito.when(postRepository.findById(123L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> {
            postService.getPostById(123L);
        }).isInstanceOf(PostNotFoundException.class);
    }

    //TESTS related to method - getAllPosts()
    @Test
    @DisplayName("Should retrieve all posts")
    void shouldRetrieveAllPosts() {

    }

    @Test
    void savePostSuccessful() {
        PostRequest request = new PostRequest();
        request.setSubredditName("subredit");
        Subreddit subreddit = new Subreddit();
        Mockito.when(subredditRepository.findByName(request.getSubredditName()))
                .thenReturn(Optional.of(subreddit));
        User user = new User();
        Post post = new Post();
        Mockito.when(authService.getCurrentUser()).thenReturn(user);
        Mockito.when(postMapper.map(request, subreddit, user)).thenReturn(post);

        postService.save(request);
        Mockito.verify(postRepository).save(post);
    }

    @Test
    void saveThrowsException() {
        PostRequest postRequest = new PostRequest();
        postRequest.setSubredditName("name");
        Assertions.assertThatThrownBy(() -> postService.save(postRequest))
                .isInstanceOf(SubredditNotFoundException.class)
                .hasMessage("name does not exist");

    }
}