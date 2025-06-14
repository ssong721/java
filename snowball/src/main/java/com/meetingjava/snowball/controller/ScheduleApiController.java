package com.meetingjava.snowball.controller;

import com.meetingjava.snowball.entity.Schedule;
import com.meetingjava.snowball.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/api/schedule")
public class ScheduleApiController {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @GetMapping
    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    @PostMapping
    public Schedule createSchedule(@RequestBody Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    @DeleteMapping("/{id}")
    public void deleteSchedule(@PathVariable Long id) {
        scheduleRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    public Schedule updateSchedule(@PathVariable Long id, @RequestBody Schedule updated) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow();
        schedule.editSchedule(
            updated.getScheduleName(),
            updated.getStartDate(),
            updated.getEndDate(),
            updated.getStartTime(),
            updated.getEndTime()
        );
        return scheduleRepository.save(schedule);
    }

    @PostMapping("/submit")
    public String saveSchedule(@RequestParam String startDate,
                                @RequestParam String endDate,
                                @RequestParam String startHour,
                                @RequestParam String startMin,
                                @RequestParam String startAMPM,
                                @RequestParam String endHour,
                                @RequestParam String endMin,
                                @RequestParam String endAMPM) {

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

        scheduleRepository.save(schedule);

        return "redirect:/dashboard/sample-meeting-id"; // 저장 후 리다이렉트
    }

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
}
