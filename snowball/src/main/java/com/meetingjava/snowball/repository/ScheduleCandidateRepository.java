package com.meetingjava.snowball.repository;

import com.meetingjava.snowball.entity.ScheduleCandidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; // ✅ 추가 필요
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ScheduleCandidateRepository extends JpaRepository<ScheduleCandidate, Long> {

    List<ScheduleCandidate> findByMeetingId(String meetingId);

    // ✅ 이거 추가해 — meetingId로 schedule_name 조회
    @Query("SELECT sc.scheduleName FROM ScheduleCandidate sc WHERE sc.meetingId = :meetingId")
    Optional<String> findFirstScheduleNameByMeetingId(@Param("meetingId") String meetingId);
}