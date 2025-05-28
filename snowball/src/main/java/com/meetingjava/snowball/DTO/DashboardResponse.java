package com.meetingjava.snowball.dto;

import java.util.List;
import com.meetingjava.snowball.entity.Schedule;

public class DashboardResponse {

    private float groupAttendanceRate;
    private Schedule nextMeeting;
    private List<Schedule> calendar;

    public DashboardResponse(float groupAttendanceRate, Schedule nextMeeting, List<Schedule> calendar) {
        this.groupAttendanceRate = groupAttendanceRate;
        this.nextMeeting = nextMeeting;
        this.calendar = calendar;
    }

    // getter/setter 생략 (필요 시 lombok 사용 가능)
}

