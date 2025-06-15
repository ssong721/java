package com.meetingjava.snowball.controller;

import com.meetingjava.snowball.dto.DashboardResponse;
import com.meetingjava.snowball.service.DashboardService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardApiController {

    private final DashboardService dashboardService;

    public DashboardApiController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // JSON 데이터 제공용 API
    @GetMapping("/{userId}/{meetingId}")
    public DashboardResponse getDashboard(@PathVariable Long userId,
            @PathVariable String meetingId) {
        return dashboardService.getDashboardData(meetingId.toString());
    }
}
