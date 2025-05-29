package com.meetingjava.snowball.repository;

import com.meetingjava.snowball.entity.ScheduleVote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleVoteRepository extends JpaRepository<ScheduleVote, String> {
    // 필요 시 커스텀 쿼리 추가 가능
    // 예: List<ScheduleVote> findByMeetingId(Long meetingId);
}
