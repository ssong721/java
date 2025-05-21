package com.meetingjava.snowball.controller;

import com.meetingjava.snowball.entity.Schedule;
import com.meetingjava.snowball.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {

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
        schedule.editSchedule(updated.getScheduleName(), updated.getScheduleDate(), updated.getStartTime(), updated.getEndTime());
        return scheduleRepository.save(schedule);
    }
}
