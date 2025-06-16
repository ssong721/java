package com.meetingjava.snowball.repository;

import com.meetingjava.snowball.entity.Check;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CheckRepository extends JpaRepository<Check, Long> {

    // 모임 ID로 체크 조회
    Optional<Check> findByMeetingId(String meetingId);

    // 모임 ID로 체크 삭제
    void deleteByMeetingId(String meetingId);
}



