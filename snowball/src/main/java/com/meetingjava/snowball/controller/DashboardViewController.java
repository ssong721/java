package com.meetingjava.snowball.controller;

import com.meetingjava.snowball.service.DashboardService;
import com.meetingjava.snowball.dto.DashboardResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/dashboard")
public class DashboardViewController {

    @GetMapping("/{meetingId}")
    public String dashboardPage(@PathVariable Long meetingId, Model model) {
        model.addAttribute("meetingId", meetingId); // JS에서 API 호출용으로 사용
        return "dashboard"; // dashboard.html 렌더링
    }
}