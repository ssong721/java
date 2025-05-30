package com.meetingjava.snowball.controller;

import com.meetingjava.snowball.entity.ScheduleVote;
import com.meetingjava.snowball.service.ScheduleVoteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ScheduleVoteViewController {

    private final ScheduleVoteService scheduleVoteService;

    public ScheduleVoteViewController(ScheduleVoteService scheduleVoteService) {
        this.scheduleVoteService = scheduleVoteService;
    }

    @GetMapping("/schedulevote")
    public String showScheduleVotePage(@RequestParam String voteId, Model model) {
        ScheduleVote vote = scheduleVoteService.getVote(voteId);

        model.addAttribute("meetingName", "모임자바라 모임");
        model.addAttribute("scheduleTitle", "정기 모임 일정 가능 시간");
        model.addAttribute("recommendedTime", vote.getRecommendedTime());
        model.addAttribute("availableUsers", vote.getAvailableUsers());

        return "schedulevote";
    }
}
