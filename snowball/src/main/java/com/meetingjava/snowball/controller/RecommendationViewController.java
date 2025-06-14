package com.meetingjava.snowball.controller;

import com.meetingjava.snowball.entity.Schedule;
import com.meetingjava.snowball.repository.ScheduleRepository;
import com.meetingjava.snowball.service.ScheduleVoteService;
import com.meetingjava.snowball.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
@Controller
public class RecommendationViewController {

    private final RecommendationService recommendationService;
    private final ScheduleVoteService scheduleVoteService;
    private final ScheduleRepository scheduleRepository;

    @GetMapping("/recommendation/{meetingId}")
    public String showRecommendationPage(@PathVariable String meetingId, Model model) {
        Map<String, Object> result = recommendationService.getRecommendedInfo(meetingId);

        Date bestTime = (Date) result.get("recommendedTime");
        List<String> availableUsers = (List<String>) result.get("availableUsers");

        String formattedTime = null;
        if (bestTime != null) {
            Timestamp timestamp = new Timestamp(bestTime.getTime());
            formattedTime = timestamp.toLocalDateTime()
                    .format(DateTimeFormatter.ofPattern("E요일 HH시", Locale.KOREAN));
        }

        model.addAttribute("meetingId", meetingId);
        model.addAttribute("recommendedTime", bestTime);
        model.addAttribute("formattedRecommendedTime", formattedTime);
        model.addAttribute("availableUsers", availableUsers);

        return "recommendation";
    }

    // ✅ fetch 대응용: ok 문자열 반환 + @ResponseBody
    @PostMapping("/recommendation/{meetingId}/confirm")
    @ResponseBody
    public String confirmSchedule(@PathVariable String meetingId,
                                  @RequestParam("date") String date,
                                  @RequestParam("startHour") int startHour,
                                  @RequestParam("startMin") int startMin,
                                  @RequestParam("endHour") int endHour,
                                  @RequestParam("endMin") int endMin) {

        LocalDate scheduleDate = LocalDate.parse(date);
        LocalTime startTime = LocalTime.of(startHour, startMin);
        LocalTime endTime = LocalTime.of(endHour, endMin);

        Schedule schedule = Schedule.builder()
                .scheduleName("확정 일정")
                .startDate(scheduleDate)
                .endDate(scheduleDate)
                .startTime(startTime)
                .endTime(endTime)
                .meetingId(meetingId)
                .build();

        scheduleRepository.save(schedule);

        return "ok";  // fetch 응답용 문자열
    }
}
