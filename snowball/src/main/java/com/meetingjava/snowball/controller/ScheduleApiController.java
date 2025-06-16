package com.meetingjava.snowball.controller;

import com.meetingjava.snowball.entity.Schedule;
import com.meetingjava.snowball.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/schedule")
public class ScheduleApiController {

    @Autowired
    private ScheduleRepository scheduleRepository;

    // 전체 일정 조회
    @GetMapping
    @ResponseBody
    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    // 일정 생성 (JSON)
    @PostMapping
    @ResponseBody
    public Schedule createSchedule(@RequestBody Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    // 일정 삭제
    @DeleteMapping("/{id}")
    @ResponseBody
    public void deleteSchedule(@PathVariable Long id) {
        scheduleRepository.deleteById(id);
    }

    // 일정 수정
    @PutMapping("/{id}")
    @ResponseBody
    public Schedule updateSchedule(@PathVariable Long id, @RequestBody Schedule updated) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow();
        schedule.editSchedule(
                updated.getScheduleName(),
                updated.getStartDate(),
                updated.getEndDate(),
                updated.getStartTime(),
                updated.getEndTime());
        return scheduleRepository.save(schedule);
    }

    // 폼 기반 일정 생성
    @PostMapping("/submit")
    public String saveSchedule(@RequestParam String startDate,
                                @RequestParam String endDate,
                                @RequestParam String startHour,
                                @RequestParam String startMin,
                                @RequestParam String startAMPM,
                                @RequestParam String endHour,
                                @RequestParam String endMin,
                                @RequestParam String endAMPM,
                                @RequestParam String meetingId) { // ✅ 여기서 meetingId도 받음 {

        System.out.println("🔥 submit 호출됨: " + startDate);

        String startTime = startHour + ":" + startMin + " " + startAMPM;
        String endTime = endHour + ":" + endMin + " " + endAMPM;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
        LocalTime start = LocalTime.parse(startTime, formatter);
        LocalTime end = LocalTime.parse(endTime, formatter);

        Schedule schedule = new Schedule();
        schedule.setStartDate(LocalDate.parse(startDate));
        schedule.setEndDate(LocalDate.parse(endDate));
        schedule.setStartTime(start);
        schedule.setEndTime(end);

        // ✅ voteId 동적 생성
        schedule.setVoteId(UUID.randomUUID().toString());

        // ✅ 진짜 meetingId 세팅
        schedule.setMeetingId(meetingId);

        scheduleRepository.save(schedule);

        // ✅ 생성한 meetingId로 리디렉션
        return "redirect:/dashboard/" + meetingId;
        }

    // 다음 확정 일정 조회
    @GetMapping("/next")
    @ResponseBody
    public Map<String, String> getNextSchedule(@RequestParam String meetingId) {
        return scheduleRepository
                .findFirstByMeetingIdAndStartDateAfterOrderByStartDateAsc(meetingId, LocalDate.now())
                .map(schedule -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("meetingName", schedule.getScheduleName());
                    map.put("dateTime", schedule.getStartDate().toString() + " " + schedule.getStartTime().toString());
                    return map;
                })
                .orElseGet(() -> Map.of("message", "예정된 모임이 없습니다."));
    }

    // ✅ FullCalendar 연동용 일정 반환 API
    @GetMapping("/calendar/events/{year}/{month}")
    @ResponseBody
    public List<Map<String, String>> getScheduleEvents(@PathVariable int year, @PathVariable int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Schedule> schedules = scheduleRepository.findByStartDateBetween(start, end);

        return schedules.stream().map(schedule -> {
            Map<String, String> event = new HashMap<>();
            event.put("title", schedule.getScheduleName());

            String startStr = schedule.getStartDate() + "T" + schedule.getStartTime();
            String endStr = schedule.getEndDate() + "T" + schedule.getEndTime();

            event.put("start", startStr);
            event.put("end", endStr);
            return event;
        }).collect(Collectors.toList());
    }
}
