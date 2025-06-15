package com.meetingjava.snowball.controller;

import com.meetingjava.snowball.entity.Check;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/check")
public class CheckController {

    @Autowired
    private Check check;

    // 출석 체크 홈
    @GetMapping("/home")
    public String showHome(@RequestParam("meetingId") String meetingId, Model model) {
        model.addAttribute("check", check);
        model.addAttribute("isEnabled", check.isEnable());
        model.addAttribute("meetingId", meetingId);
        return "checkHome";
    }

    // 출석 퀴즈 생성 폼
    @GetMapping("/form")
    public String showCheckForm(@RequestParam("meetingId") String meetingId, Model model) {
        model.addAttribute("meetingId", meetingId);
        return "checkForm";
    }

    // 출석 퀴즈 생성 처리
    @PostMapping("/create")
    public String createCheck(@RequestParam String question,
                              @RequestParam String answer,
                              @RequestParam String meetingId) {
        check.checkSet(question, "subjective");
        check.checkRun(answer);
        check.checkOn();
        return "redirect:/check/home?meetingId=" + meetingId;
    }

    // 출석 퀴즈 답변 폼
    @GetMapping("/answer")
    public String answerForm(@RequestParam("meetingId") String meetingId, Model model) {
        model.addAttribute("check", check);
        model.addAttribute("meetingId", meetingId);
        return "checkAnswer";
    }

    // 출석 퀴즈 답변 제출
    @PostMapping("/submit")
    public String submitAnswer(@RequestParam String userAnswer, Model model) {
        boolean isCorrect = check.checkCode(userAnswer);
        model.addAttribute("result", isCorrect ? "출석 완료!" : "오답입니다. 출석 실패!");
        return "checkResult";
    }
}


