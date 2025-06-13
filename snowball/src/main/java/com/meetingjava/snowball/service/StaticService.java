package com.meetingjava.snowball.service;

import org.springframework.stereotype.Component;
import com.meetingjava.snowball.entity.Schedule;
import java.time.LocalDate;
import java.util.*;

@Component
public class StaticService {

    private final List<Schedule> allSchedules;
    private final Map<String, Double> attendanceRates; // meetingId → 출석률

    // 예시: userAttendanceRates 저장 (username+meetingId → 출석률)
    private final Map<String, Double> userAttendanceRates;

    public StaticService(List<Schedule> allSchedules) {
        this.allSchedules = allSchedules;
        this.attendanceRates = new HashMap<>();
        this.userAttendanceRates = new HashMap<>();
    }

    public float calculateAttendanceRate(String meetingId) {
        // 여기선 단순히 map에서 가져오고, 없으면 0f
        return attendanceRates.getOrDefault(meetingId, 0.0).floatValue();
    }

    // 유저 출석률 계산 메서드 추가
    public double calculateUserAttendanceRate(String username, String meetingId) {
        String key = username + "_" + meetingId;
        return userAttendanceRates.getOrDefault(key, 0.0);
    }

    public List<Integer> getMonthlyMeetingCounts() {
        Map<Integer, Integer> countPerMonth = new HashMap<>();
        for (Schedule schedule : allSchedules) {
            int month = schedule.getStartDate().getMonthValue();
            countPerMonth.put(month, countPerMonth.getOrDefault(month, 0) + 1);
        }
        return new ArrayList<>(countPerMonth.values());
    }

    public void updateAttendanceRate(String meetingId, double rate) {
        attendanceRates.put(meetingId, rate);
    }

    public void updateUserAttendanceRate(String username, String meetingId, double rate) {
        String key = username + "_" + meetingId;
        userAttendanceRates.put(key, rate);
    }

    public List<Schedule> getAllSchedules() {
        return allSchedules;
    }

    public Optional<Schedule> getUpcomingSchedule(String meetingId) {
        LocalDate now = LocalDate.now();
        return allSchedules.stream()
                .filter(s -> s.getMeetingId().equals(meetingId))
                .filter(s -> s.getStartDate().isAfter(now))
                .sorted(Comparator.comparing(Schedule::getStartDate))
                .findFirst();
    }

    public Optional<Schedule> getTodaySchedule(String meetingId) {
        LocalDate today = LocalDate.now();
        return allSchedules.stream()
                .filter(s -> s.getMeetingId().equals(meetingId)) // meetingId 필터링 추가
                .filter(s -> s.getStartDate().isEqual(today))
                .findFirst();
    }
}
