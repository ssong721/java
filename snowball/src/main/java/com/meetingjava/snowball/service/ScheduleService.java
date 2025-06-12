package com.meetingjava.snowball.service;

import com.meetingjava.snowball.entity.Schedule;
import com.meetingjava.snowball.entity.ScheduleVote;
import com.meetingjava.snowball.entity.VoteSubmission;
import com.meetingjava.snowball.repository.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public Optional<Schedule> getUpcomingSchedule(String meetingId) {
        return scheduleRepository.findFirstByMeetingIdAndScheduleDateAfterOrderByScheduleDateAsc(
    meetingId, LocalDate.now());
    }

    public Optional<Schedule> getTodaySchedule(String meetingId) {
        return scheduleRepository.findByMeetingIdAndScheduleDate(
            meetingId, LocalDate.now());
    }
    public List<Schedule> getSchedulesByMonth(int year, int month) {
        LocalDateTime start = LocalDate.of(year, month, 1).atStartOfDay();
        LocalDateTime end = start.plusMonths(1).minusNanos(1);
    
        return scheduleRepository.findByScheduleDateBetween(start.toLocalDate(), end.toLocalDate());
    }
}
