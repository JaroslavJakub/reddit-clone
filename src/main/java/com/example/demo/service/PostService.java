package com.example.demo.service;


import java.util.List;

import org.hibernate.transform.ToListResultTransformer;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class PostService {

	private final PostRepository postRepository;
	private final SubredditRepository subredditRepository;
	private final UserRepository userRepository;
	private final AuthService authService;
	private final PostMapper postMapper;
	
	public void save(PostRequest postRequest) {
		Subreddit subreddit = subredditRepository.findByName(postRequest.getSubredditName())
				.orElseThrow(()-> new SubredditNotFoundException(postRequest.getSubredditName()));
		postRepository.save(postMapper.map(postRequest, subreddit, authService.getCurrentUser()));
	}
	
	@Transactional(readOnly = true)
	public PostResponse getPostById(Long id) {
		Post post = postRepository.findById(id)
				.orElseThrow(()-> new PostNotFoundException(id.toString()));
		return postMapper.mapToDto(post);
	}
	
	@Transactional(readOnly = true)
	public List<PostResponse> getAllPosts() {
		return postRepository.findAll()
				.stream()
				.map(postMapper::mapToDto)
				.collect(toList());
	}
	
	@Transactional(readOnly = true)
	public List<PostResponse> getPostsBySubreddit(Long subredditId) {
		Subreddit subreddit = subredditRepository.findById(subredditId)
				.orElseThrow(()-> new SubredditNotFoundException(subredditId.toString()));
		List<Post> posts = postRepository.findAllBySubreddit(subreddit);
		return posts.stream().map(postMapper::mapToDto).collect(toList());
	}
	
	@Transactional(readOnly = true)
	public List<PostResponse> getPostsByUsername(String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(()-> new UsernameNotFoundException(username));
		List<Post> posts = postRepository.findByUser(user);
		return posts.stream().map(postMapper::mapToDto).collect(toList());
	}
	
}


