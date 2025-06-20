package com.meetingjava.snowball.controller;

import com.meetingjava.snowball.entity.Schedule;
import com.meetingjava.snowball.entity.Calendar;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import com.meetingjava.snowball.dto.ScheduleEventdto;



@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    private final Calendar calendar;  // Calendar 컴포넌트

    public CalendarController(Calendar calendar) {
        this.calendar = calendar;
    }

    /**
     * FullCalendar 전용: 특정 연·월 스케줄 (title, start, end만 포함)
     * GET /api/calendar/full-events/{year}/{month}
     */
    @GetMapping("/full-events/{year}/{month}") 
    public List<ScheduleEventdto> getFullCalendarEvents(@PathVariable int year,
                                                         @PathVariable int month) {
        List<Schedule> schedules = calendar.getScheduleForMonth(year, month);

        return schedules.stream()
            .map(s -> new ScheduleEventdto(
                s.getScheduleName(),
                s.getStart(),
                s.getEnd()
            ))
            .toList();
    }

    /**
     * 백엔드 내부 로직용: Schedule 전체 내려줌 (엔티티 통째로)
     * GET /api/calendar/events/{year}/{month}
     */
    @GetMapping("/events/{year}/{month}")
    public List<Schedule> getMonthEvents(@PathVariable int year,
                                         @PathVariable int month) {
        return calendar.getScheduleForMonth(year, month);
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