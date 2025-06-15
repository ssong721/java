package com.meetingjava.snowball.dashboard;

import com.meetingjava.snowball.entity.Check;
import com.meetingjava.snowball.service.ScheduleService;
import com.meetingjava.snowball.service.StaticService;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class Dashboard {

    private final ScheduleService scheduleService;
    private final StaticService staticService;
    private final Check checkService;

    private String meeting;
    private float attendanceRate;
    private int totalMeetingCount;
    private Date upcomingMeeting;
    private Date todayMeeting;

    public Dashboard(ScheduleService scheduleService,
            StaticService staticService,
            Check checkService) {
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

        if (checkService != null && checkServiceEnable()) {
            checkService.checkOn();
        }
    }

    public Summary getSummary() {
        return new Summary(meeting, attendanceRate, totalMeetingCount, upcomingMeeting, todayMeeting);
    }

    private boolean checkServiceEnable() {
        try {
            return (boolean) Check.class.getDeclaredField("enable").get(checkService);
        } catch (Exception e) {
            return false;
        }
    }
}
