package com.meetingjava.snowball.service;

import com.meetingjava.snowball.entity.Schedule;
import com.meetingjava.snowball.repository.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        return scheduleRepository.findFirstByMeetingIdAndStartDateAfterOrderByStartDateAsc(
                meetingId, LocalDate.now());
    }

    public Optional<Schedule> getTodaySchedule(String meetingId) {
        return scheduleRepository.findByMeetingIdAndStartDate(
                meetingId, LocalDate.now());
    }

    public List<Schedule> getSchedulesByMonth(int year, int month) {
        LocalDateTime start = LocalDate.of(year, month, 1).atStartOfDay();
        LocalDateTime end = start.plusMonths(1).minusNanos(1);

        return scheduleRepository.findByStartDateBetween(start.toLocalDate(), end.toLocalDate());
    }

    // 일정 제목이 있는 일정만 반환 (임시 일정 제거 목적)
    public List<Schedule> getSchedulesByMeetingAndMonth(String meetingId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return scheduleRepository.findByMeetingIdAndStartDateBetween(meetingId, start, end)
                .stream()
                .filter(s -> s.getScheduleName() != null && !s.getScheduleName().isBlank())
                .toList();
    }
}
