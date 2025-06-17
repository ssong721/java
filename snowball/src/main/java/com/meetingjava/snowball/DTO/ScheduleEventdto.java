package com.meetingjava.snowball.dto;

import com.meetingjava.snowball.entity.Schedule;

public class ScheduleEventdto {
    private String title;
    private String start;
    private String end;

    // 기본 생성자 꼭 필요 (for JSON serialization)
    public ScheduleEventdto() {
    }

    public ScheduleEventdto(String title, String start, String end) {
        this.title = title;
        this.start = start;
        this.end = end;
    }

    public static ScheduleEventdto from(Schedule schedule) {
        String startTime = schedule.getStartTime().toString();  
        String endTime = schedule.getEndTime().toString();      
        String name = schedule.getScheduleName();            
    
        // 출력 포맷: 모임명 시작~종료
        String fullTitle = name + " " + startTime + " ~ " + endTime;

    return new ScheduleEventdto(
        fullTitle,
        schedule.getStart(), // 시작 ISO_DATETIME
        schedule.getEnd()    // 종료 ISO_DATETIME
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
