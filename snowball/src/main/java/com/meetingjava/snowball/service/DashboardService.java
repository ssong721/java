package com.meetingjava.snowball.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.meetingjava.snowball.entity.Schedule;
import com.meetingjava.snowball.service.StaticService;
import com.meetingjava.snowball.dto.DashboardResponse;



@Service
public class DashboardService {

    private final StaticService staticService;

    public DashboardService(StaticService staticService) {
        this.staticService = staticService;
    }

    public DashboardResponse getDashboardData(String meetingId) {
        // 1. 우리 모임 출석률
        float groupRate = staticService.calculateAttendanceRate(meetingId);

        // 2. 다음 모임
        Schedule nextMeeting = staticService.getUpcomingSchedule(meetingId)
                                            .orElse(null);

        // 3. 이번 달 캘린더
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        LocalDate end = start.plusMonths(1).minusDays(1);
        List<Schedule> calendarList = staticService.getAllSchedules().stream()
                .filter(s -> s.getMeetingId().equals(meetingId))
                .filter(s -> {
                    LocalDate date = s.getScheduleDate();
                    return (date.isEqual(start) || date.isAfter(start)) &&
                           (date.isEqual(end) || date.isBefore(end));
                })
                .toList();

        return new DashboardResponse(groupRate, nextMeeting, calendarList);
    }
}
