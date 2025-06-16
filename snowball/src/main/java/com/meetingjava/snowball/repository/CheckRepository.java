package com.meetingjava.snowball.repository;

import com.meetingjava.snowball.entity.Check;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CheckRepository extends JpaRepository<Check, Long> {
    Optional<Check> findByMeetingId(String meetingId);
    void deleteByMeetingId(String meetingId);
}

