package com.meetingjava.snowball.controller;

import com.meetingjava.snowball.service.DashboardService;
import com.meetingjava.snowball.dto.DashboardResponse;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/{userId}/{meetingId}")
public DashboardResponse getDashboard(@PathVariable Long userId,
                                      @PathVariable Long meetingId) {
    // 지금은 userId 안 쓰니까 meetingId만 넘겨!
    return dashboardService.getDashboardData(meetingId.toString());
    }
}



