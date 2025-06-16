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

        // ✅ 추천 시간과 가장 먼저 일치하는 후보 일정 가져오기
        Optional<ScheduleCandidate> matchingCandidate = Optional.empty();
        if (bestTime != null) {
            List<ScheduleCandidate> candidates = scheduleCandidateRepository.findByMeetingId(meetingId);
            matchingCandidate = candidates.stream()
                .filter(c -> {
                    LocalDate cDate = c.getStartDate();
                    LocalTime cTime = c.getStartTime();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(bestTime);
                    return cDate.equals(LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)))
                        && cTime.equals(LocalTime.of(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)));
                })
                .findFirst();
        }

        Long candidateId = matchingCandidate.map(ScheduleCandidate::getId).orElse(null);

        model.addAttribute("meetingId", meetingId);
        model.addAttribute("recommendedTime", bestTime);
        model.addAttribute("formattedRecommendedTime", formattedTime);
        model.addAttribute("availableUsers", availableUsers);
        model.addAttribute("candidateId", candidateId);  // ✅ HTML에 넘기기

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

        // ✅ schedule_name을 후보 일정에서 가져오기
        String scheduleName = "확정 일정";
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
