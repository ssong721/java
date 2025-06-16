package com.meetingjava.snowball.repository;

import com.meetingjava.snowball.entity.VoteSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoteSubmissionRepository extends JpaRepository<VoteSubmission, Long> {

    // ✅ ScheduleVote.voteId 기준 조회 (반드시 있어야 함)
    List<VoteSubmission> findByVote_VoteId(String voteId);

    // ✅ ScheduleVote.meetingId 기준 조회
    List<VoteSubmission> findByVote_MeetingId(String meetingId);
}
