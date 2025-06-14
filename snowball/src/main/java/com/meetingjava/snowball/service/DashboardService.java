package com.meetingjava.snowball.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.meetingjava.snowball.entity.Schedule;
import com.meetingjava.snowball.service.StaticService;
import com.meetingjava.snowball.dto.DashboardResponse;

@Service
public class DashboardService {

    private final StaticService staticService;
    private final NoticeService noticeService;

    public DashboardService(StaticService staticService, NoticeService noticeService) {
        this.staticService = staticService;
        this.noticeService = noticeService;
    }

    // 대시보드 전체 데이터 (API 용)
    public DashboardResponse getDashboardData(String meetingId) {
        float groupRate = staticService.calculateAttendanceRate(meetingId);
        Schedule nextMeeting = staticService.getUpcomingSchedule(meetingId).orElse(null);

        LocalDate start = LocalDate.now().withDayOfMonth(1);
        LocalDate end = start.plusMonths(1).minusDays(1);
        List<Schedule> calendarList = staticService.getAllSchedules().stream()
                .filter(s -> s.getMeetingId().equals(meetingId))
                .filter(s -> {
                    LocalDate date = s.getStartDate();
                    return !date.isBefore(start) && !date.isAfter(end);
                })
                .toList();

        String noticeTitle = "공지사항 없음";

        return new DashboardResponse(groupRate, nextMeeting, calendarList, noticeTitle);
    }

    // ✔️ 컨트롤러용 메서드들

    public double getGroupAttendanceRate(String meetingId) {
        return staticService.calculateAttendanceRate(String.valueOf(meetingId));
    }

    public double getUserAttendanceRate(String username, String meetingId) {
        return staticService.calculateUserAttendanceRate(username, String.valueOf(meetingId));
    }

    public String getNextMeetingInfo(String meetingId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return staticService.getUpcomingSchedule(String.valueOf(meetingId))
                .map(s -> s.getStartDate().format(formatter) + " - " + s.getScheduleName())
                .orElse("예정된 모임이 없습니다.");
    }
}
