package com.meetingjava.snowball.controller;

import com.meetingjava.snowball.entity.Attendance;
import com.meetingjava.snowball.entity.Check;
import com.meetingjava.snowball.service.CheckService;
import com.meetingjava.snowball.repository.AttendanceRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.time.LocalDate;

@Controller
@RequestMapping("/check")
public class CheckController {

    private final CheckService checkService;
    private final AttendanceRepository attendanceRepository;

    public CheckController(CheckService checkService, AttendanceRepository attendanceRepository) {
        this.checkService = checkService;
        this.attendanceRepository = attendanceRepository;
    }

    // 출석 체크 홈
    @GetMapping("/home")
    public String showHome(@RequestParam("meetingId") String meetingId, Model model) {
        Check check = checkService.getByMeetingId(meetingId).orElse(null);
        boolean isEnabled = check != null && check.isEnable();

        model.addAttribute("check", check);
        model.addAttribute("isEnabled", isEnabled);
        model.addAttribute("meetingId", meetingId);
        return "checkHome";
    }

    // 퀴즈 생성 폼
    @GetMapping("/form")
    public String showCheckForm(@RequestParam("meetingId") String meetingId, Model model) {
        model.addAttribute("meetingId", meetingId);
        return "checkForm";
    }

    // 퀴즈 생성 처리
    @PostMapping("/create")
    public String createCheck(@RequestParam String question,
                              @RequestParam String answer,
                              @RequestParam String meetingId) {
        checkService.create(meetingId, question, answer);
        return "redirect:/check/home?meetingId=" + meetingId;
    }

    // 퀴즈 답변 폼
    @GetMapping("/answer")
    public String answerForm(@RequestParam("meetingId") String meetingId, Model model) {
        Check check = checkService.getByMeetingId(meetingId).orElse(null);
        model.addAttribute("check", check);
        model.addAttribute("meetingId", meetingId);
        return "checkAnswer";
    }

    // 퀴즈 제출 및 출석 저장
    @PostMapping("/submit")
    @Transactional
    public String submitAnswer(@RequestParam String userAnswer,
                               @RequestParam String meetingId,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model) {
        boolean isCorrect = checkService.isCorrectAnswer(meetingId, userAnswer);
        LocalDate today = LocalDate.now();

        if (isCorrect && userDetails != null) {
            String username = userDetails.getUsername();

            boolean alreadyExists = attendanceRepository
                    .existsByUsernameAndMeetingIdAndAttendanceDate(username, meetingId, today);

            if (!alreadyExists) {
                Attendance attendance = Attendance.builder()
                        .username(username)
                        .meetingId(meetingId)
                        .attendanceDate(today)
                        .present(true)
                        .build();
                attendanceRepository.save(attendance);
            }
        }

        model.addAttribute("result", isCorrect ? "출석 완료!" : "오답입니다. 출석 실패!");
        model.addAttribute("meetingId", meetingId);
        return "checkResult";
    }

    // 출석 퀴즈 삭제 (퀴즈 + 출석기록)
    @PostMapping("/delete")
    public String deleteCheck(@RequestParam("meetingId") String meetingId) {
        checkService.deleteByMeetingId(meetingId);
        return "redirect:/check/home?meetingId=" + meetingId;
    }

    // 햄버거 메뉴용 퀴즈 관리
    @GetMapping("/manage")
    public String managePage(@RequestParam("meetingId") String meetingId, Model model) {
        Check check = checkService.getByMeetingId(meetingId).orElse(null);
        model.addAttribute("check", check);
        model.addAttribute("meetingId", meetingId);
        return "checkManage";
    }
}
