package com.meetingjava.snowball.controller;

import com.meetingjava.snowball.entity.Check;
import com.meetingjava.snowball.service.CheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/check")
public class CheckController {

    @Autowired
    private CheckService checkService;

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

    // 답변 제출
    @PostMapping("/submit")
    public String submitAnswer(@RequestParam String userAnswer,
                               @RequestParam String meetingId,
                               Model model) {
        boolean isCorrect = checkService.isCorrectAnswer(meetingId, userAnswer);
        model.addAttribute("result", isCorrect ? "출석 완료!" : "오답입니다. 출석 실패!");
        model.addAttribute("meetingId", meetingId);
        return "checkResult";
    }

    // 질문 삭제 (햄버거 메뉴)
    @PostMapping("/delete")
    public String deleteCheck(@RequestParam("meetingId") String meetingId) {
        checkService.deleteByMeetingId(meetingId);
        return "redirect:/check/home?meetingId=" + meetingId;
    }
}




