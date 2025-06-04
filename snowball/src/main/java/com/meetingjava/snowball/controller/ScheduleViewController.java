package com.meetingjava.snowball.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ScheduleViewController {

    @GetMapping("/schedulemake")
    public String showScheduleMakePage() {
        return "schedulemake"; // templates/create-schedule.html 렌더링됨
    }
}
