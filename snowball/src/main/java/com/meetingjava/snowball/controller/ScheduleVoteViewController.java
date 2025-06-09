package com.meetingjava.snowball.controller;

import com.meetingjava.snowball.entity.ScheduleVote;
import com.meetingjava.snowball.service.MeetingService;
import com.meetingjava.snowball.service.ScheduleVoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ScheduleVoteViewController {

    private final ScheduleVoteService scheduleVoteService;
    private final MeetingService meetingService;

    @GetMapping("/schedulevote/{meetingId}")
    public String showScheduleVotePage(@PathVariable String meetingId, Model model) {
        ScheduleVote vote;

        try {
            vote = scheduleVoteService.findByMeetingId(meetingId);
        } catch (NoSuchElementException e) {
            Date now = new Date();
            Date threeDaysLater = new Date(now.getTime() + 3L * 24 * 60 * 60 * 1000);
            vote = scheduleVoteService.createVote(now, threeDaysLater, 60, meetingId);
        }

        String meetingName = meetingService.getMeetingNameById(meetingId);

        // ✅ Date → Timestamp 변환 (여기 수정!)
        Date recommendedTimeRaw = vote.getRecommendedTime();
        Timestamp recommendedTime = recommendedTimeRaw != null ? new Timestamp(recommendedTimeRaw.getTime()) : null;

        String formattedTime = null;
        if (recommendedTime != null) {
            formattedTime = recommendedTime.toLocalDateTime()
                    .format(DateTimeFormatter.ofPattern("E요일 HH시", Locale.KOREAN));
        }

        // 모델에 값 주입
        model.addAttribute("voteId", vote.getVoteId());
        model.addAttribute("meetingId", vote.getMeetingId());
        model.addAttribute("meetingName", meetingName);
        model.addAttribute("scheduleTitle", "모임 가능 시간");
        model.addAttribute("formattedRecommendedTime", formattedTime);
        model.addAttribute("recommendedTime", recommendedTime); // 꼭 남겨둬도 됨
        model.addAttribute("availableUsers", Optional.ofNullable(vote.getAvailableUsers()).orElse(new ArrayList<>()));

        return "schedulevote";
    }
}
