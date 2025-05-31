package com.meetingjava.snowball.controller;

import com.meetingjava.snowball.entity.Meeting;
import com.meetingjava.snowball.service.DashboardService;
import com.meetingjava.snowball.service.MeetingService;
import com.meetingjava.snowball.service.NoticeService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

@Controller
@RequestMapping("/dashboard")
public class DashboardViewController {

    private final DashboardService dashboardService;
    private final MeetingService meetingService;
    private final NoticeService noticeService;

    public DashboardViewController(DashboardService dashboardService,
                                   MeetingService meetingService,
                                   NoticeService noticeService) {
        this.dashboardService = dashboardService;
        this.meetingService = meetingService;
        this.noticeService = noticeService;
    }

    @GetMapping("/{meetingId}")
    public String dashboardPage(@PathVariable String meetingId,
                                Model model,
                                @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        // 1) 모임 존재 여부 체크 및 정보 조회 (필요하면)
        Meeting meeting = meetingService.findById(meetingId);
        if (meeting == null) {
            return "redirect:/error"; // 모임 없으면 에러 페이지로 이동하거나 다른 처리
        }

        // 2) 출석률 및 다음 모임, 공지사항 등 데이터 조회
        double groupAttendanceRate = dashboardService.getGroupAttendanceRate(meetingId);
        double myAttendanceRate = dashboardService.getUserAttendanceRate(username, meetingId);
        String nextMeeting = dashboardService.getNextMeetingInfo(meetingId);
        String noticeTitle = noticeService.getLatestNoticeTitle(meetingId);

        // 3) 모델에 데이터 넣기
        model.addAttribute("meetingName", meeting.getMeetingName());
        model.addAttribute("meetingId", meetingId);
        model.addAttribute("groupAttendanceRate", groupAttendanceRate);
        model.addAttribute("myAttendanceRate", myAttendanceRate);
        model.addAttribute("nextMeeting", nextMeeting);
        model.addAttribute("noticeTitle", noticeTitle);

        return "dashboard";
    }
}
