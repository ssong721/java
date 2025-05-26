package com.meetingjava.snowball.service;

import com.meetingjava.snowball.entity.Schedule;
import com.meetingjava.snowball.repository.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        return scheduleRepository.findFirstByMeetingIdAndScheduleDateAfterOrderByScheduleDateAsc(
    meetingId, LocalDate.now());
    }

    public Optional<Schedule> getTodaySchedule(String meetingId) {
        return scheduleRepository.findByMeetingIdAndScheduleDate(
            meetingId, LocalDate.now());
    }
}
