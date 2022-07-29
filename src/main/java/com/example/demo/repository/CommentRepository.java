package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.Comment;
import com.example.demo.model.Post;
import com.example.demo.model.User;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	
	List<Comment> findByPost(Post post);
	
	List<Comment> findAllByUser(User user);

}
