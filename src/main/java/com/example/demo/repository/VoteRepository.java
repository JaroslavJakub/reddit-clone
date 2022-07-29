package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Post;
import com.example.demo.model.User;
import com.example.demo.model.Vote;

public interface VoteRepository extends JpaRepository<Vote, Long>{
	
	Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);

}
