package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.PostRequest;
import com.example.demo.dto.PostResponse;
import com.example.demo.service.PostService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping(path = "/api/posts")
@AllArgsConstructor
public class PostController {
	
	private final PostService postService;
//	createPost
//	getAllPosts
//	getPost
//	getPostsBySubreddit
//	getPostsByUsername
	
	@PostMapping
	public ResponseEntity<HttpStatus> createPost(@RequestBody PostRequest postRequest) {
		postService.save(postRequest);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@GetMapping
	public ResponseEntity<List<PostResponse>> getAllPosts() {
		return ResponseEntity.status(HttpStatus.OK).body(postService.getAllPosts());
	}
	
	@GetMapping(path = "/{id}")
	public ResponseEntity<PostResponse> getPostById(@PathVariable Long id) {
		return ResponseEntity.status(HttpStatus.OK).body(postService.getPostById(id));
	}
	
	@GetMapping(path = "by-subreddit/{id}") 
	public ResponseEntity<List<PostResponse>> getPostsBySubreddit(@PathVariable Long id) {
		return ResponseEntity.status(HttpStatus.OK).body(postService.getPostsBySubreddit(id));
	}
	
	@GetMapping(path = "by-username/{username}")
	public ResponseEntity<List<PostResponse>> getPostsByUsername(@PathVariable String username) {
		return ResponseEntity.status(HttpStatus.OK).body(postService.getPostsByUsername(username));
	}

}
