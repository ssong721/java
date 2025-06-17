package com.meetingjava.snowball.service;

import com.meetingjava.snowball.entity.Attendance;
import com.meetingjava.snowball.entity.Meeting;
import com.meetingjava.snowball.entity.Schedule;
import com.meetingjava.snowball.repository.AttendanceRepository;
import com.meetingjava.snowball.repository.MeetingRepository;
import com.meetingjava.snowball.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StaticService {

    private final AttendanceRepository attendanceRepository;
    private final MemberRepository memberRepository;
    private final MeetingRepository meetingRepository;
    private final List<Schedule> allSchedules;

    public StaticService(AttendanceRepository attendanceRepository,
                         MemberRepository memberRepository,
                         MeetingRepository meetingRepository) {
        this.attendanceRepository = attendanceRepository;
        this.memberRepository = memberRepository;
        this.meetingRepository = meetingRepository;
        this.allSchedules = new ArrayList<>();
    }

    // 전체 출석률 자동 계산 (모임 기준)
    public float calculateAttendanceRate(String meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
        .orElseThrow(() -> new IllegalArgumentException("해당 모임이 존재하지 않습니다."));

        int totalMembers = memberRepository.countByMeeting(meeting);
        if (totalMembers == 0) return 0f;

        int presentCount = attendanceRepository.countPresentByMeetingId(meetingId);

        return (float) presentCount / totalMembers * 100f;
    }


    // 사용자 출석률 자동 계산 (모임 기준)
    public double calculateUserAttendanceRate(String username, String meetingId) {
        List<Attendance> records = attendanceRepository.findByUsernameAndMeetingId(username, meetingId);
        if (records.isEmpty()) return 0.0;

        long presentCount = records.stream()
                .filter(Attendance::isPresent)
                .count();

        return (double) presentCount / records.size() * 100.0;
    }

    // 월별 모임 횟수 계산
    public List<Integer> getMonthlyMeetingCounts() {
        Map<Integer, Integer> countPerMonth = new HashMap<>();
        for (Schedule schedule : allSchedules) {
            int month = schedule.getStartDate().getMonthValue();
            countPerMonth.put(month, countPerMonth.getOrDefault(month, 0) + 1);
        }
        return new ArrayList<>(countPerMonth.values());
    }

    // 오늘 이후 예정된 모임 찾기
    public Optional<Schedule> getUpcomingSchedule(String meetingId) {
        LocalDate now = LocalDate.now();
        return allSchedules.stream()
                .filter(s -> s.getMeetingId().equals(meetingId))
                .filter(s -> s.getStartDate().isAfter(now))
                .sorted(Comparator.comparing(Schedule::getStartDate))
                .findFirst();
    }

    // 오늘 날짜의 모임 찾기
    public Optional<Schedule> getTodaySchedule(String meetingId) {
        LocalDate today = LocalDate.now();
        return allSchedules.stream()
                .filter(s -> s.getMeetingId().equals(meetingId))
                .filter(s -> s.getStartDate().isEqual(today))
                .findFirst();
    }

    // 일정 수동 등록 (테스트용 or 수동 계산용)
    public void setAllSchedules(List<Schedule> schedules) {
        this.allSchedules.clear();
        this.allSchedules.addAll(schedules);
    }

    public List<Schedule> getAllSchedules() {
        return allSchedules;
    }

    public double calculateAccurateAttendanceRate(String meetingId, List<Schedule> schedules, int totalMembers) {
        if (schedules == null || schedules.isEmpty() || totalMembers == 0) return 0.0;

        double totalRate = 0.0;

        for (Schedule schedule : schedules) {
            int presentCount = attendanceRepository.countPresentMembers(meetingId, schedule.getId());
            totalRate += ((double) presentCount / totalMembers);
        }

        return (totalRate / schedules.size()) * 100.0;
    }
}

