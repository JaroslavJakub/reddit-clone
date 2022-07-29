package com.example.demo.service;

import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.dto.CommentDto;
import com.example.demo.exception.PostNotFoundException;
import com.example.demo.mapper.CommentMapper;
import com.example.demo.model.Comment;
import com.example.demo.model.NotificationEmail;
import com.example.demo.model.Post;
import com.example.demo.model.User;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;

import lombok.AllArgsConstructor;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class CommentService {
	
	private static final String POST_URL = "";
	
	private final PostRepository postRepository;
	private final CommentRepository commentRepository;
	private final CommentMapper commentMapper;
	private final AuthService authService;
	private final MailService mailService;
	private final MailContentBuilder mailContentBuilder;
	private final UserRepository userRepository;
	
	public void save(CommentDto commentDto) {
		Post post = postRepository.findById(commentDto.getPostId())
				.orElseThrow(()-> new PostNotFoundException(commentDto.getPostId().toString()));
		Comment comment = commentMapper.map(commentDto, post, authService.getCurrentUser());
		commentRepository.save(comment);
		
		//String postUrl = comment.getPost().getUrl();
		
		String message = mailContentBuilder.build(post.getUser().getUsername() + " posted a comment on your post." + POST_URL);
		sendCommentNotification(message, post.getUser());
	}
	private void sendCommentNotification(String message, User user) {
		mailService.sendMail(new NotificationEmail(user.getUsername() + " commented on your post", user.getEmail(), message));
	}
	
	public List<CommentDto> getAllCommentsForPost(Long postId) {
		Post post = postRepository.findById(postId)
				.orElseThrow(()-> new PostNotFoundException(postId.toString()));
		return commentRepository.findByPost(post)
				.stream()
				.map(commentMapper::mapToDto)
				.collect(toList());
	}
	
	public List<CommentDto> getAllCommentsForUser(String userName) {
		User user = userRepository.findByUsername(userName)
				.orElseThrow(()-> new UsernameNotFoundException(userName));
		return commentRepository.findAllByUser(user)
				.stream()
				.map(commentMapper::mapToDto)
				.collect(toList());
	}

}
