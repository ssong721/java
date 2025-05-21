package com.meetingjava.snowball.service;

import com.meetingjava.snowball.entity.Schedule;
import java.time.LocalDate;
import java.util.*;

public class StaticService {

    private final List<Schedule> allSchedules;
    private final Map<String, Double> attendanceRates;  // meetingId → 출석률

    public StaticService(List<Schedule> allSchedules) {
        this.allSchedules = allSchedules;
        this.attendanceRates = new HashMap<>();
    }

    public float calculateAttendanceRate(String meetingId) {
        // 여기선 단순히 map에서 가져오고, 없으면 0f
        return attendanceRates.getOrDefault(meetingId, 0.0).floatValue();
    }

    public List<Integer> getMonthlyMeetingCounts() {
        Map<Integer, Integer> countPerMonth = new HashMap<>();
        for (Schedule schedule : allSchedules) {
            int month = schedule.getScheduleDate().getMonthValue();
            countPerMonth.put(month, countPerMonth.getOrDefault(month, 0) + 1);
        }
        return new ArrayList<>(countPerMonth.values());
    }

    public void updateAttendanceRate(String meetingId, double rate) {
        attendanceRates.put(meetingId, rate);
    }

    public List<Schedule> getAllSchedules() {
        return allSchedules;
    }

    public Optional<Schedule> getUpcomingSchedule(String meetingId) {
        LocalDate now = LocalDate.now();
        return allSchedules.stream()
                .filter(s -> s.getScheduleDate().isAfter(now))
                .sorted(Comparator.comparing(Schedule::getScheduleDate))
                .findFirst();
    }

    public Optional<Schedule> getTodaySchedule(String meetingId) {
        LocalDate today = LocalDate.now();
        return allSchedules.stream()
                .filter(s -> s.getScheduleDate().isEqual(today))
                .findFirst();
    }
}
