package com.meetingjava.snowball.dto;

import java.util.List;
import com.meetingjava.snowball.entity.Schedule;

public class DashboardResponse {

    private float groupAttendanceRate;
    private Schedule nextMeeting;
    private List<Schedule> calendar;
    private String noticeTitle; // 🔹 새 필드 추가

    public DashboardResponse(float groupAttendanceRate, Schedule nextMeeting, List<Schedule> calendar, String noticeTitle) {
        this.groupAttendanceRate = groupAttendanceRate;
        this.nextMeeting = nextMeeting;
        this.calendar = calendar;
        this.noticeTitle = noticeTitle;
    }

    // 🔹 Getter 추가
    public float getGroupAttendanceRate() {
        return groupAttendanceRate;
    }

    public Schedule getNextMeeting() {
        return nextMeeting;
    }

    public List<Schedule> getCalendar() {
        return calendar;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }
}

