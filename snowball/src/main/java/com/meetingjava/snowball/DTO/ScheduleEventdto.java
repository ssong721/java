package com.meetingjava.snowball.dto;

import com.meetingjava.snowball.entity.Schedule;

public class ScheduleEventdto {
    private String title;
    private String start;
    private String end;

    // ✅ 기본 생성자 꼭 필요 (for JSON serialization)
    public ScheduleEventdto() {
    }

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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
