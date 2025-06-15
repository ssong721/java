package com.meetingjava.snowball.repository;

import com.meetingjava.snowball.entity.ScheduleCandidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleCandidateRepository extends JpaRepository<ScheduleCandidate, Long> {
    List<ScheduleCandidate> findByMeetingId(String meetingId);
}
