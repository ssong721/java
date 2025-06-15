package com.meetingjava.snowball.repository;

import com.meetingjava.snowball.entity.ScheduleVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScheduleVoteRepository extends JpaRepository<ScheduleVote, String> {
    Optional<ScheduleVote> findByMeetingId(String meetingId);
    void deleteByMeetingId(String meetingId);
}
