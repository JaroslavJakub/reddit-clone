package com.example.demo.repository;

import com.example.demo.model.Post;
import com.example.demo.model.User;
import com.example.demo.model.Vote;
import com.example.demo.model.VoteType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);

    @Modifying
    @Query("UPDATE Vote v SET v.voteType = ?1 WHERE v.voteId = ?2")
    void updateVoteTypeById(VoteType voteType, Long voteId);

}
