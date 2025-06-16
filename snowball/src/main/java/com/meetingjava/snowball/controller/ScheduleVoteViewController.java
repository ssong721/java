package com.meetingjava.snowball.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetingjava.snowball.entity.ScheduleVote;
import com.meetingjava.snowball.entity.ScheduleCandidate;
import com.meetingjava.snowball.repository.ScheduleCandidateRepository;
import com.meetingjava.snowball.service.MeetingService;
import com.meetingjava.snowball.service.ScheduleVoteService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class ScheduleVoteViewController {

    private final ScheduleVoteService scheduleVoteService;
    private final MeetingService meetingService;
    private final ObjectMapper objectMapper;
    private final ScheduleCandidateRepository scheduleCandidateRepository;

    @GetMapping("/schedulevote/{voteId}")
    public String showVotePage(@PathVariable String voteId, Model model) throws JsonProcessingException {
        // 투표 정보 조회
        ScheduleVote vote = scheduleVoteService.getVote(voteId);
        String meetingId = vote.getMeetingId();

        // 후보 일정 조회
        List<ScheduleCandidate> candidates = scheduleCandidateRepository.findByVoteId(voteId);
        Set<String> allowedTimes = new HashSet<>();

        // ISO 포맷: "yyyy-MM-dd'T'HH:mm:ss"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        for (ScheduleCandidate candidate : candidates) {
            LocalDateTime current = LocalDateTime.of(candidate.getStartDate(), candidate.getStartTime());
            LocalDateTime endTime = LocalDateTime.of(candidate.getEndDate(), candidate.getEndTime());

            // 시간 단위로 반복 (종료 시간은 포함하지 않음)
            while (!current.isAfter(endTime.minusMinutes(1))) {
                allowedTimes.add(current.format(formatter));
                current = current.plusHours(1);
            }
        }

        // JSON으로 변환
        String allowedTimesJson = objectMapper.writeValueAsString(allowedTimes);

        // 모델 전달
        model.addAttribute("voteId", voteId);
        model.addAttribute("meetingId", meetingId);
        model.addAttribute("allowedTimesJson", allowedTimesJson);

        return "schedulevote";
    }
}
