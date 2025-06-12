package com.meetingjava.snowball.controller;

import com.meetingjava.snowball.entity.ScheduleVote;
import com.meetingjava.snowball.service.ScheduleVoteService;
import com.meetingjava.snowball.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
@Controller
public class RecommendationViewController {

    private final RecommendationService recommendationService;
    private final ScheduleVoteService scheduleVoteService;  // ✅ 이 줄 반드시 추가해줘야 함!

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
        model.addAttribute("recommendedTime", bestTime);  // ✅ 아래 HTML에서 사용하기 위해 원본도 넘김
        model.addAttribute("formattedRecommendedTime", formattedTime);
        model.addAttribute("availableUsers", availableUsers);

        return "recommendation";
    }

    @PostMapping("/recommendation/{meetingId}/confirm")
    public String confirmTime(@PathVariable String meetingId,
                              @RequestParam("selectedTime") String selectedTime) {
        ScheduleVote vote = scheduleVoteService.findByMeetingId(meetingId);
        scheduleVoteService.updateRecommendedTime(vote.getVoteId(), selectedTime);
        return "redirect:/dashboard/" + meetingId;
    }
}