package com.meetingjava.snowball.controller;

import com.meetingjava.snowball.entity.Meeting;
import com.meetingjava.snowball.service.MeetingService;

import jakarta.servlet.http.HttpSession;

import com.meetingjava.snowball.dto.Meetingdto;
import com.meetingjava.snowball.dto.HomeDto;

import org.springframework.stereotype.Controller;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import com.meetingjava.snowball.repository.MeetingRepository;
import org.springframework.ui.Model;
import java.util.List;

import java.time.LocalDateTime;
import java.util.Date;

@Controller
public class MeetingController {

    private final MeetingService meetingService;
    @Autowired
    private MeetingRepository meetingRepository;

    public MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @PostMapping("/newmeeting")
    public String createMeeting(@RequestParam("meetingName") String meetingName, 
                                @AuthenticationPrincipal UserDetails userDetails) {

        // TODO: 로그인한 사용자에서 모임장 정보 가져오기 (예시로 "host1" 고정)
        String hostUsername = userDetails.getUsername();

        meetingService.createMeeting(meetingName, hostUsername);

        return "redirect:/home";  // 홈 화면으로 리디렉션
    }

    @GetMapping("/newmeeting")
    public String CreateMeetingForm() {
        return "newmeeting";  // newmeeting.html
    }

    @GetMapping("/home")
    public String showHome(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        List<HomeDto> homes = meetingService.getHomesForUser(username);
        model.addAttribute("homes", homes);

        return "home";  // home.html 템플릿
    }

}
