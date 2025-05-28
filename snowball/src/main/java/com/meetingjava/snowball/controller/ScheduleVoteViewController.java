package com.meetingjava.snowball.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ScheduleVoteViewController {

    @GetMapping("/schedulevote")
    public String showScheduleVotePage(Model model) {
        model.addAttribute("meetingName", "모임자바라 모임"); // 동적으로 넣을 수도 있음
        model.addAttribute("scheduleTitle", "정기 모임 일정 가능 시간");
        return "schedulevote";
    }
}

