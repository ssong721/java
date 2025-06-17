package com.meetingjava.snowball.repository;

import com.meetingjava.snowball.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // 오늘 일정 조회
    Optional<Schedule> findByMeetingIdAndStartDate(String meetingId, LocalDate date);

    // 다가오는 가장 빠른 일정 조회
    Optional<Schedule> findFirstByMeetingIdAndStartDateAfterOrderByStartDateAsc(String meetingId, LocalDate date);

    // 일정 월 단위 조회
    List<Schedule> findByStartDateBetween(LocalDate start, LocalDate end);

    // 특정 모임에 대한 월 단위 일정 조회
    List<Schedule> findByMeetingIdAndStartDateBetween(String meetingId, LocalDate start, LocalDate end);

    // 데이터 삭제
    void deleteByMeetingId(String meetingId);

    List<Schedule> findByMeetingId(String meetingId);
}
