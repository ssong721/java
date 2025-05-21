package com.meetingjava.snowball.dashboard;

import java.util.Date;

/**
 * Notion 없이 만든 Dashboard 의 요약 DTO
 */
public class Summary {
    private final String meeting;
    private final float  attendanceRate;
    private final int    totalMeetingCount;
    private final Date   upcomingMeeting;
    private final Date   todayMeeting;

    public Summary(String meeting,
                   float attendanceRate,
                   int totalMeetingCount,
                   Date upcomingMeeting,
                   Date todayMeeting) {
        this.meeting           = meeting;
        this.attendanceRate    = attendanceRate;
        this.totalMeetingCount = totalMeetingCount;
        this.upcomingMeeting   = upcomingMeeting;
        this.todayMeeting      = todayMeeting;
    }

    public String getMeeting()           { return meeting; }
    public float  getAttendanceRate()    { return attendanceRate; }
    public int    getTotalMeetingCount() { return totalMeetingCount; }
    public Date   getUpcomingMeeting()   { return upcomingMeeting; }
    public Date   getTodayMeeting()      { return todayMeeting; }
}
