package com.meetingjava.snowball.controller;

import com.meetingjava.snowball.entity.ScheduleVote;
import com.meetingjava.snowball.service.MeetingService;
import com.meetingjava.snowball.service.ScheduleVoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
public class ScheduleVoteViewController {

    private final ScheduleVoteService scheduleVoteService;
    private final MeetingService meetingService;

    @GetMapping("/schedulevote/{meetingId}")
    public String showScheduleVotePage(@PathVariable String meetingId, Model model) {
        ScheduleVote vote;

        try {
            // ✅ meetingId로 기존 투표 조회
            vote = scheduleVoteService.findByMeetingId(meetingId);
        } catch (NoSuchElementException e) {
            // ❗투표가 없다면 새로 생성
            Date now = new Date();
            Date threeDaysLater = new Date(now.getTime() + 3L * 24 * 60 * 60 * 1000); // 3일 뒤
            vote = scheduleVoteService.createVote(now, threeDaysLater, 60, meetingId);
        }

        // ✅ meetingName 조회
        String meetingName = meetingService.getMeetingNameById(meetingId);

        // ✅ 모델에 정보 추가
        model.addAttribute("voteId", vote.getVoteId()); // ✔️ 투표 고유 ID
        model.addAttribute("meetingId", vote.getMeetingId()); // ✔️ 모임 ID
        model.addAttribute("meetingName", meetingName); // ✔️ 모임 이름
        model.addAttribute("scheduleTitle", "모임 가능 시간");
        model.addAttribute("recommendedTime", vote.getRecommendedTime()); // ✔️ 추천 시간
        model.addAttribute("availableUsers", vote.getAvailableUsers()); // ✔️ 가능한 인원 리스트

        return "schedulevote"; // 👉 templates/schedulevote.html 로 렌더링
    }
}
