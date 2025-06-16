package com.meetingjava.snowball.controller;

import com.meetingjava.snowball.entity.Meeting;
import com.meetingjava.snowball.dto.ScheduleEventdto;
import com.meetingjava.snowball.service.DashboardService;
import com.meetingjava.snowball.service.MeetingService;
import com.meetingjava.snowball.service.NoticeService;
import com.meetingjava.snowball.service.ScheduleService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class DashboardViewController {

    private final DashboardService dashboardService;
    private final MeetingService meetingService;
    private final NoticeService noticeService;
    private final ScheduleService scheduleService;

    public DashboardViewController(DashboardService dashboardService,
                                   MeetingService meetingService,
                                   NoticeService noticeService,
                                   ScheduleService scheduleService) {
        this.dashboardService = dashboardService;
        this.meetingService = meetingService;
        this.noticeService = noticeService;
        this.scheduleService = scheduleService;
    }

    @GetMapping("")
    public String dashboardDefaultRedirect() {
        return "redirect:/dashboard/sample-meeting-id";
    }

    @GetMapping("/api/calendar/full-events/{year}/{month}")
    @ResponseBody
    public List<ScheduleEventdto> getFullCalendarEvents(@PathVariable int year,
                                                        @PathVariable int month) {
        return scheduleService.getSchedulesByMonth(year, month).stream()
                .map(ScheduleEventdto::from)
                .toList();
    }

    @GetMapping("/{meetingId:.+}")
    public String dashboardPage(@PathVariable String meetingId,
                                Model model,
                                @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            System.out.println("⚠️ 로그인 정보 없음, 로그인 페이지로 리다이렉트");
            return "redirect:/login";
        }

        String username = userDetails.getUsername();
        System.out.println("대시보드 요청 도착: " + meetingId);

        Meeting meeting = meetingService.findById(meetingId);
        if (meeting == null) {
            return "redirect:/error";
        }

        double groupAttendanceRate = dashboardService.getGroupAttendanceRate(meetingId);
        double myAttendanceRate = dashboardService.getUserAttendanceRate(username, meetingId);
        String nextMeeting = dashboardService.getNextMeetingInfo(meetingId);
        String noticeTitle = noticeService.getLatestNoticeTitle(meetingId);

        model.addAttribute("meetingName", meeting.getMeetingName());
        model.addAttribute("meetingId", meetingId);
        model.addAttribute("groupAttendanceRate", groupAttendanceRate);
        model.addAttribute("myAttendanceRate", myAttendanceRate);
        model.addAttribute("nextMeeting", nextMeeting);
        model.addAttribute("noticeTitle", noticeTitle);

        return "dashboard";
    }

    @GetMapping("/api/calendar/events/{year}/{month}")
    @ResponseBody
    public List<ScheduleEventdto> getCalendarEventsByMeetingId(@PathVariable int year,
                                                               @PathVariable int month,
                                                               @RequestParam String meetingId) {
        return scheduleService.getSchedulesByMeetingAndMonth(meetingId, year, month).stream()
                .map(ScheduleEventdto::from)
                .toList();
    }
}
