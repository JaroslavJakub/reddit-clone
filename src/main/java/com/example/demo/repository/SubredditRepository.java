package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.Subreddit;

public interface SubredditRepository extends JpaRepository<Subreddit, Long> {
	
	Optional<Subreddit> findByName(String subredditName);

}
