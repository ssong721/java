package com.meetingjava.snowball.controller;

import com.meetingjava.snowball.entity.Schedule;
import com.meetingjava.snowball.entity.ScheduleCandidate;
import com.meetingjava.snowball.repository.ScheduleCandidateRepository;
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
    private final ScheduleCandidateRepository scheduleCandidateRepository;

    @GetMapping("/recommendation/{voteId}")
    public String showRecommendationPage(@PathVariable("voteId") String voteId, Model model) {
        voteId = voteId.replaceAll("\"", ""); // ✅ 따옴표 제거

        Map<String, Object> result = scheduleVoteService.getRecommendedTimeInfoByVoteId(voteId);

        Date bestTime = (Date) result.get("recommendedTime");
        List<String> availableUsers = (List<String>) result.get("availableUsers");

        String formattedTime = null;
        if (bestTime != null) {
            Timestamp timestamp = new Timestamp(bestTime.getTime());
            formattedTime = timestamp.toLocalDateTime()
                    .format(DateTimeFormatter.ofPattern("E요일 HH시", Locale.KOREAN));
        }

        // ✅ voteId로 직접 일정 후보 가져오기
        Optional<ScheduleCandidate> candidateOpt = scheduleCandidateRepository.findByVoteId(voteId);
        String scheduleName = candidateOpt.map(ScheduleCandidate::getScheduleName).orElse("확정 일정");
        Long candidateId = candidateOpt.map(ScheduleCandidate::getId).orElse(null);
        String meetingId = candidateOpt.map(ScheduleCandidate::getMeetingId).orElse("unknown");

        model.addAttribute("voteId", voteId);
        model.addAttribute("meetingId", meetingId);
        model.addAttribute("scheduleName", scheduleName);
        model.addAttribute("recommendedTime", bestTime);
        model.addAttribute("formattedRecommendedTime", formattedTime);
        model.addAttribute("availableUsers", availableUsers);
        model.addAttribute("candidateId", candidateId);

        return "recommendation";
    }

    @PostMapping("/recommendation/{meetingId}/confirm")
    @ResponseBody
    public String confirmSchedule(@PathVariable String meetingId,
                                  @RequestParam("startDate") String startDateStr,
                                  @RequestParam("endDate") String endDateStr,
                                  @RequestParam("startHour") int startHour,
                                  @RequestParam("startMin") int startMin,
                                  @RequestParam("endHour") int endHour,
                                  @RequestParam("endMin") int endMin,
                                  @RequestParam(value = "candidateId", required = false) String candidateIdStr) {

        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);
        LocalTime startTime = LocalTime.of(startHour, startMin);
        LocalTime endTime = LocalTime.of(endHour, endMin);

        String scheduleName = "확정 일정";  // 기본값

        try {
            if (candidateIdStr != null && !candidateIdStr.isBlank()) {
                Long candidateId = Long.parseLong(candidateIdStr);
                Optional<ScheduleCandidate> candidateOpt = scheduleCandidateRepository.findById(candidateId);
                scheduleName = candidateOpt.map(ScheduleCandidate::getScheduleName).orElse("확정 일정");
            }
        } catch (Exception e) {
            System.out.println("❌ candidateId 파싱 실패: " + candidateIdStr);
        }

        Schedule schedule = Schedule.builder()
                .scheduleName(scheduleName)
                .startDate(startDate)
                .endDate(endDate)
                .startTime(startTime)
                .endTime(endTime)
                .meetingId(meetingId)
                .build();

        scheduleRepository.save(schedule);
        return "ok";
    }
}
