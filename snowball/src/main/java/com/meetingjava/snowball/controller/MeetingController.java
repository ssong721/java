package com.meetingjava.snowball.controller;

import com.meetingjava.snowball.entity.Meeting;
import com.meetingjava.snowball.entity.User;
import com.meetingjava.snowball.service.MeetingService;
import com.meetingjava.snowball.service.MemberService;
import com.meetingjava.snowball.repository.MeetingRepository;
import com.meetingjava.snowball.repository.UserRepository;

import com.meetingjava.snowball.dto.HomeDto;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.servlet.http.HttpSession;

import java.security.Principal;
import java.util.Optional;
import java.util.List;

@Controller
public class MeetingController {

    private final MeetingService meetingService;
    private MeetingRepository meetingRepository;
    private final MemberService memberService;
    private final UserRepository userRepository;

    public MeetingController(MeetingService meetingService,
                             MeetingRepository meetingRepository,
                             MemberService memberService,
                             UserRepository userRepository) {
        this.meetingService = meetingService;
        this.meetingRepository = meetingRepository;
        this.memberService = memberService;
        this.userRepository = userRepository;
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

    //홈화면으로 연결
    @GetMapping("/home")
    public String homePage(Model model, 
                        @AuthenticationPrincipal UserDetails userDetails,
                        HttpSession session) {
        if (userDetails != null) {
            String username = userDetails.getUsername();

            // 이름 정보도 같이 담기 (세션에서 가져오거나 UserDetails에서 직접 가져오기)
            User loginUser = (User) session.getAttribute("loginUser");
            if (loginUser != null) {
                model.addAttribute("username", loginUser.getName());
            } else {
                model.addAttribute("username", username); // fallback
            }

            // 사용자 관련 홈 정보 조회
            List<HomeDto> homes = meetingService.getHomesForUser(username);
            model.addAttribute("homes", homes);
        }

        return "home";
    }

    // 모임 상세 조회
    @GetMapping("/meeting/{id}")
    public String viewMeeting(@PathVariable String id, Model model) {
        Optional<Meeting> meetingOpt = meetingRepository.findById(id);
        if (meetingOpt.isPresent()) {
            model.addAttribute("meeting", meetingOpt.get());
            return "meetingView";
        } else {
            return "error/404";
        }
    }

    @GetMapping("/join-meeting")
    public String joinMeetingForm() {
        return "join-meeting"; // 위 HTML 파일로 이동
    }


    @PostMapping("/join-meeting")
    public String joinMeeting(@RequestParam String meetingId, Principal principal, HttpSession session) {
        // 현재 로그인된 사용자
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("유저 없음"));

        Meeting meeting = meetingRepository.findById(meetingId)
            .orElseThrow(() -> new RuntimeException("모임 없음"));

        memberService.joinMeeting(user, meeting);

        // 세션에 사용자 정보 업데이트 (홈에서 이름 표시를 위해)
        session.setAttribute("loginUser", user);

        return "redirect:/home";
    }


    @GetMapping("/search-meeting")
    public String searchMeeting(@RequestParam("keyword") String keyword, Model model) {
        List<Meeting> meetings = meetingRepository.findByMeetingNameContaining(keyword); // 모임 이름에 키워드 포함
        model.addAttribute("meetings", meetings);
        return "search-meeting"; // 위에서 만든 html
    }
    
}