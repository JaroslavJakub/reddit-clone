package com.example.demo.mapper;

import com.example.demo.dto.CommentDto;
import com.example.demo.model.Comment;
import com.example.demo.model.Post;
import com.example.demo.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

class CommentMapperTest {

    private CommentMapperImpl commentMapperImpl = new CommentMapperImpl();

    @Test
    void test() {
        Comment comment = new Comment();
        comment.setCreatedDate(Instant.ofEpochMilli(389747324));
        comment.setId(2L);
        comment.setPost(Post.builder().postId(1L).build());
        comment.setText("asjkffjka");
        comment.setUser(User.builder().username("Jetro").build());

        CommentDto commentDto = commentMapperImpl.mapToDto(comment);

        org.assertj.core.api.Assertions.assertThat(commentDto.getCreatedDate()).isEqualTo(comment.getCreatedDate());
        org.assertj.core.api.Assertions.assertThat(commentDto.getId()).isEqualTo(comment.getId());
        org.assertj.core.api.Assertions.assertThat(commentDto.getPostId()).isEqualTo(comment.getPost().getPostId());
        org.assertj.core.api.Assertions.assertThat(commentDto.getText()).isEqualTo(comment.getText());
        org.assertj.core.api.Assertions.assertThat(commentDto.getUserName()).isEqualTo(comment.getUser().getUsername());


    }

    @Test
    public void testImproved() {
        Comment comment = new Comment(
                1L,
                "asdasd",
                Post.builder().postId(1L).build(),
                Instant.now(),
                User.builder().username("Jetro").build());

        CommentDto commentDto = commentMapperImpl.mapToDto(comment);

        Assertions.assertThat(commentDto).usingRecursiveComparison().ignoringFields("postId", "userName");
        Assertions.assertThat(commentDto.getPostId()).isEqualTo(comment.getPost().getPostId());
        Assertions.assertThat(commentDto.getUserName()).isEqualTo(comment.getUser().getUsername());
    }

    @Test
    void testNull() {
        CommentDto commentDto = commentMapperImpl.mapToDto(null);
        org.assertj.core.api.Assertions.assertThat(commentDto).isNull();
    }

}
