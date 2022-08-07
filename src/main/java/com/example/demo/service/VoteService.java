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

import static com.example.demo.model.VoteType.DOWNVOTE;
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
			switch (voteDto.getVoteType()) {
				case UPVOTE :
					post.setVoteCount(post.getVoteCount() - 1);
					voteRepository.deleteById(voteByPostAndUser.get().getVoteId());
					postRepository.save(post);
					break;
				case DOWNVOTE :
					post.setVoteCount(post.getVoteCount() + 1);
					voteRepository.deleteById(voteByPostAndUser.get().getVoteId());
					postRepository.save(post);
					break;
			}
//			throw new SpringRedditException("You have already " + voteDto.getVoteType() + "'d for this post.");
//			throw new SpringRedditException("Vote has been removed");
		}
		
		if (voteByPostAndUser.isPresent() && !(voteByPostAndUser.get().getVoteType().equals(voteDto.getVoteType()))) {
			switch (voteDto.getVoteType()) {
				case UPVOTE :
					post.setVoteCount(post.getVoteCount() + 2);
					voteRepository.updateVoteTypeById(UPVOTE, voteByPostAndUser.get().getVoteId());
					break;
				case DOWNVOTE :
					post.setVoteCount(post.getVoteCount() - 2);
					voteRepository.updateVoteTypeById(DOWNVOTE, voteByPostAndUser.get().getVoteId());
					break;
			}
//			voteRepository.save(mapToVote(voteDto, post));
			postRepository.save(post);
		}
		
		if (!voteByPostAndUser.isPresent()) {
			switch (voteDto.getVoteType()) {
				case UPVOTE :
					post.setVoteCount(post.getVoteCount() + 1);
					break;
				case DOWNVOTE :
					post.setVoteCount(post.getVoteCount() - 1);
					break;
				}
			voteRepository.save(mapToVote(voteDto, post));
			postRepository.save(post);
		}
	}

}
