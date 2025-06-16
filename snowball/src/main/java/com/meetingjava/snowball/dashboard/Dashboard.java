package com.meetingjava.snowball.dashboard;

import com.meetingjava.snowball.service.ScheduleService;
import com.meetingjava.snowball.service.StaticService;
import com.meetingjava.snowball.service.CheckService;

import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class Dashboard {

    private final ScheduleService scheduleService;
    private final StaticService staticService;
    private final CheckService checkService;

    private String meeting;
    private float attendanceRate;
    private int totalMeetingCount;
    private Date upcomingMeeting;
    private Date todayMeeting;

    public Dashboard(ScheduleService scheduleService,
                     StaticService staticService,
                     CheckService checkService) {
        this.scheduleService = scheduleService;
        this.staticService = staticService;
        this.checkService = checkService;
    }

    public void setMeeting(String meetingId) {
        this.meeting = meetingId;
    }

    public void refresh() {
        if (meeting == null) {
            throw new IllegalStateException("meeting ID is null. setMeeting() 먼저 호출 필요.");
        }

        this.attendanceRate = staticService.calculateAttendanceRate(meeting);
        this.totalMeetingCount = staticService.getMonthlyMeetingCounts()
                .stream()
                .mapToInt(Integer::intValue)
                .sum();

        staticService.getUpcomingSchedule(meeting)
                .ifPresent(s -> this.upcomingMeeting = java.sql.Date.valueOf(s.getStartDate()));

        staticService.getTodaySchedule(meeting)
                .ifPresent(s -> this.todayMeeting = java.sql.Date.valueOf(s.getStartDate()));

        // 출석 퀴즈가 존재하는지 확인만
        if (checkService.getByMeetingId(meeting).isPresent()) {
            System.out.println("✅ 출석 퀴즈가 존재합니다.");
        }
    }

    public Summary getSummary() {
        return new Summary(meeting, attendanceRate, totalMeetingCount, upcomingMeeting, todayMeeting);
    }
}
