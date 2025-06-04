package com.meetingjava.snowball.repository;

import com.meetingjava.snowball.entity.VoteSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoteSubmissionRepository extends JpaRepository<VoteSubmission, Long> {
    List<VoteSubmission> findByVoteId(String voteId);
}
