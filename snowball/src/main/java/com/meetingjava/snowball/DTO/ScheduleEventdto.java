package com.meetingjava.snowball.dto;

import com.meetingjava.snowball.entity.Schedule;

public class ScheduleEventdto {
    private String title;
    private String start;
    private String end;

    public ScheduleEventdto(String title, String start, String end) {
        this.title = title;
        this.start = start;
        this.end = end;
    }
    
    public static ScheduleEventdto from(Schedule schedule) {
        return new ScheduleEventdto(
            schedule.getScheduleName(),
            schedule.getStart(), // ISO_DATETIME
            schedule.getEnd()
        );
    }

    public String getTitle() {
        return title;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

}

