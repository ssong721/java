package com.meetingjava.snowball.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetingjava.snowball.entity.ScheduleVote;
import com.meetingjava.snowball.entity.ScheduleCandidate;
import com.meetingjava.snowball.service.MeetingService;
import com.meetingjava.snowball.service.ScheduleVoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class ScheduleVoteViewController {

    private final ScheduleVoteService scheduleVoteService;
    private final MeetingService meetingService;
    private final ObjectMapper objectMapper; // ✅ 설정된 ObjectMapper를 Spring이 주입함

    // ✅ URL에서 voteId를 받음!
    @GetMapping("/schedulevote/{voteId}")
    public String showScheduleVotePage(@PathVariable String voteId, Model model) throws Exception {
        // ✅ voteId로 직접 조회
        ScheduleVote vote = scheduleVoteService.findById(voteId);
        String meetingId = vote.getMeetingId(); // 진짜 meetingId 추출

        String meetingName = meetingService.getMeetingNameById(meetingId);
        Date recommendedTimeRaw = vote.getRecommendedTime();
        Timestamp recommendedTime = recommendedTimeRaw != null ? new Timestamp(recommendedTimeRaw.getTime()) : null;

        String formattedTime = null;
        if (recommendedTime != null) {
            formattedTime = recommendedTime.toLocalDateTime()
                    .format(DateTimeFormatter.ofPattern("E요일 HH시", Locale.KOREAN));
        }

        // 후보 일정 조회
        List<ScheduleCandidate> candidates = scheduleVoteService.getCandidates(voteId);
        String candidatesJson = objectMapper.writeValueAsString(candidates);

        model.addAttribute("voteId", vote.getVoteId());
        model.addAttribute("meetingId", meetingId); // ✅ 정확한 meetingId
        model.addAttribute("meetingName", meetingName);
        model.addAttribute("scheduleTitle", "모임 가능 시간");
        model.addAttribute("formattedRecommendedTime", formattedTime);
        model.addAttribute("recommendedTime", recommendedTime);
        model.addAttribute("availableUsers", Optional.ofNullable(vote.getAvailableUsers()).orElse(new ArrayList<>()))
             .addAttribute("candidatesJson", candidatesJson);

        return "schedulevote";
    }
}
