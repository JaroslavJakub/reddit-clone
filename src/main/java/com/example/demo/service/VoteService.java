package com.example.demo.service;


import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.VoteDto;
import com.example.demo.exception.PostNotFoundException;
import com.example.demo.exception.SpringRedditException;
import com.example.demo.model.Post;
import com.example.demo.model.Vote;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.VoteRepository;

import lombok.AllArgsConstructor;

import static com.example.demo.model.VoteType.UPVOTE;;

@Service
@AllArgsConstructor
public class VoteService {
	
	private final AuthService authService;
	private final VoteRepository voteRepository;
	private final PostRepository postRepository;
	
	private Vote mapToVote(VoteDto voteDto, Post post) {
		return Vote.builder()
				.voteType(voteDto.getVoteType())
				.post(post)
				.user(authService.getCurrentUser())
				.build();
	}
	
	@Transactional
	public void vote(VoteDto voteDto) {
		Post post = postRepository.findById(voteDto.getPostId())
				.orElseThrow(()-> new PostNotFoundException("There is no post with ID: " + voteDto.getPostId().toString()));
		
		Optional<Vote> voteByPostAndUser = voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post, authService.getCurrentUser());
		
		if (voteByPostAndUser.isPresent() && voteByPostAndUser.get().getVoteType().equals(voteDto.getVoteType())) {
			throw new SpringRedditException("You have already " + voteDto.getVoteType() + "'d for this post.");
		}
		
		if(UPVOTE.equals(voteDto.getVoteType())) {
			post.setVoteCount(post.getVoteCount() + 1);
		} else {
			post.setVoteCount(post.getVoteCount() - 1);
		}
		voteRepository.save(mapToVote(voteDto, post));
		postRepository.save(post);
	}

}
