package com.meetingjava.snowball.dto;

public class ScheduleEventdto {
    private String title;
    private String start;
    private String end;

    public ScheduleEventdto(String title, String start, String end) {
        this.title = title;
        this.start = start;
        this.end = end;
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

