package com.meetingjava.snowball.controller;

import com.meetingjava.snowball.entity.Schedule;
import com.meetingjava.snowball.entity.Meeting;
import com.meetingjava.snowball.entity.Calendar;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    private final Calendar calendar;  // Calendar 컴포넌트

    public CalendarController(Calendar calendar) {
        this.calendar = calendar;
    }

    /**
     * 1) 특정 연·월의 스케줄 전체 조회
     * GET /api/calendar/events/{year}/{month}
     */
    @GetMapping("/events/{year}/{month}")
    public List<Schedule> getMonthEvents(@PathVariable int year,
                                         @PathVariable int month) {
        return calendar.getScheduleForMonth(year, month);
    }

    /**
     * 2) 오늘 날짜의 미팅만 조회
     * GET /api/calendar/meetings/today
     */
    @GetMapping("/meetings/today")
    public List<Meeting> getTodayMeetings() {
        return calendar.getTodayMeeting();
    }

    /**
     * 3) 새 스케줄 등록
     * POST /api/calendar/events
     * Body: Schedule JSON
     */
    @PostMapping("/events")
    public void addEvent(@RequestBody Schedule schedule) {
        calendar.registerSchedule(schedule);
    }

    /**
     * 4) 미팅 일시 변경
     * PUT /api/calendar/meetings/{id}?dateTime=2025-05-20T15:30:00
     */
    @PutMapping("/meetings/{id}")
    public void reschedule(@PathVariable String id,
                           @RequestParam String dateTime) {
        LocalDateTime dt = LocalDateTime.parse(dateTime);
        calendar.rescheduleMeeting(id, dt);
    }
}
