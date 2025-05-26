package com.meetingjava.snowball.repository;

import com.meetingjava.snowball.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // 오늘 일정
    Optional<Schedule> findByMeetingIdAndScheduleDate(String meetingId, LocalDate date);

    // 다가오는 가장 빠른 일정
    Optional<Schedule> findFirstByMeetingIdAndScheduleDateAfterOrderByScheduleDateAsc(String meetingId, LocalDate date);
}
