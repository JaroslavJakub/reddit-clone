package com.example.demo.service;

import com.example.demo.dto.CommentDto;
import com.example.demo.exception.SpringRedditException;
import com.example.demo.mapper.CommentMapper;
import com.example.demo.model.Comment;
import com.example.demo.model.User;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock

    private CommentMapper commentMapper;
    @Mock

    private AuthService authService;
    @Mock

    private MailService mailService;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    void getAllCommentsForUser_userNotFound_exceptionThrown() {
        Mockito.when(userRepository.findByUsername("Jetro")).thenReturn(Optional.empty());

        Assertions.assertThrows(UsernameNotFoundException.class
                , () -> commentService.getAllCommentsForUser("Jetro"));
    }

    @Test
    void getAllCommentsForUser_worksFine() {
        User user = new User();
        CommentDto commentDto = new CommentDto();
        Comment comment = new Comment();
        Mockito.when(userRepository.findByUsername("Jetro")).thenReturn(Optional.of(user));
        Mockito.when(commentRepository.findAllByUser(user)).thenReturn(Arrays.asList(comment));
        Mockito.when(commentMapper.mapToDto(comment)).thenReturn(commentDto);

        org.assertj.core.api.Assertions.assertThat(commentService.getAllCommentsForUser("Jetro")).containsExactly(commentDto);


    }

    //	Without using AssertJ
    @Test
    @DisplayName("Test should pass when comment do not contain swear words")
    void shouldNotContainSwearWordsInsideComment() {
//		CommentService commentService = new CommentService(null,null,null, null, null,null);
        assertFalse(commentService.containsSwearWords("This is a comment"));
    }

    @Test
    @DisplayName("Should throw exception when comment contain swear words")
    void shouldFailWhenCommentContainsSwearWords() {
//		CommentService commentService = new CommentService(null, null, null, null,null, null);
        SpringRedditException exception = assertThrows(SpringRedditException.class, () -> {
            commentService.containsSwearWords("This is shitty comment");
        });
        assertTrue(exception.getMessage().contains("Comments contains unacceptable language"));
    }

    //	Improved by using AssertJ fluentAPI
    @Test
    @DisplayName("Test should pass when comment do not contain swear words ASSERTJ")
    void shouldNotContainSwearWordsInsideCommentAJ() {
//		CommentService commentService = new CommentService(null,null,null,null, null,null);
        assertThat(commentService.containsSwearWords("This is a comment")).isFalse();
    }

    @Test
    @DisplayName("Should throw exception when comment contain swear words ASSERTJ")
    void shouldThrowExceptionWhenCommentContainSwearWords() {
//		CommentService commentService = new CommentService(null,null,null, null, null, null);

        assertThatThrownBy(() -> {
            commentService.containsSwearWords("This is shitty comment");
        }).isInstanceOf(SpringRedditException.class)
                .hasMessage("Comments contains unacceptable language");
    }

}
