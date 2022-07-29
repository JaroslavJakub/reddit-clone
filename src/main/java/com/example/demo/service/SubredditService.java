package com.example.demo.service;



import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.stream.Collectors.toList;

import java.util.List;

import com.example.demo.dto.SubredditDto;
import com.example.demo.exception.SpringRedditException;
import com.example.demo.mapper.SubredditMapper;
import com.example.demo.model.Subreddit;
import com.example.demo.repository.SubredditRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SubredditService {
	
	private final SubredditRepository subredditRepository;
	private final SubredditMapper subredditMapper;
	
	@Transactional
	public SubredditDto saveSubreddit(SubredditDto subredditDto) {
		Subreddit subreddit = subredditRepository.save(subredditMapper.mapDtoToSubreddit(subredditDto));
		subredditDto.setId(subreddit.getId());
		return subredditDto;
	}
	
	@Transactional(readOnly = true)
	public List<SubredditDto> getAllSubreddits() {
		return subredditRepository.findAll()
				.stream()
				.map(subredditMapper::mapSubredditToDto)
				.collect(toList());
	}
	
	public SubredditDto getSubreddit(Long id) {
		return subredditMapper.mapSubredditToDto(subredditRepository.findById(id).orElseThrow(() -> new SpringRedditException(
				"User with ID: " + id + "was not found!")));
	}

}
