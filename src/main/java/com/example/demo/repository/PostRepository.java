package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.Post;
import com.example.demo.model.Subreddit;
import com.example.demo.model.User;

public interface PostRepository extends JpaRepository<Post, Long> {
	
	List<Post> findAllBySubreddit(Subreddit subreddit);
	
	List<Post> findByUser(User user);
	
}
