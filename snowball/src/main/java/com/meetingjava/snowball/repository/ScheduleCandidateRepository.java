package com.meetingjava.snowball.repository;

import com.meetingjava.snowball.entity.ScheduleCandidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleCandidateRepository extends JpaRepository<ScheduleCandidate, Long> {

    List<ScheduleCandidate> findByMeetingId(String meetingId);

    // ✅ meetingId로 scheduleName 조회 (예: 추천 일정 확정용)
    @Query("SELECT sc.scheduleName FROM ScheduleCandidate sc WHERE sc.meetingId = :meetingId")
    Optional<String> findFirstScheduleNameByMeetingId(@Param("meetingId") String meetingId);

    // ❌ 오류 원인 → ✅ 수정!
    List<ScheduleCandidate> findByVoteId(String voteId);
}