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

    // 출석 퀴즈 생성 폼
    @GetMapping("/form")
    public String showCheckForm() {
        return "checkForm";  // 퀴즈 생성하는 HTML
    }

    // 출석 퀴즈 생성 처리
    @PostMapping("/create")
    public String createCheck(@RequestParam String question,
                              @RequestParam String answer) {
        check.checkSet(question, "subjective");
        check.checkRun(answer);
        check.checkOn();
        return "redirect:/check/answer";
    }

    // 출석 퀴즈 통합 화면
    @GetMapping("/answer")
    public String showCheckPage(Model model) {
        model.addAttribute("check", check);
        model.addAttribute("isEnabled", check.isEnable());
        model.addAttribute("message", check.isEnable() ? "출석 퀴즈가 생성되었습니다." : "아직 출석 퀴즈가 생성되지 않았습니다!");
        return "check";  // check.html 하나로 통합
    }

    // 출석 제출 처리
    @PostMapping("/submit")
    public String submitAnswer(@RequestParam String userAnswer, Model model) {
        boolean isCorrect = check.checkCode(userAnswer);
        model.addAttribute("result", isCorrect ? "출석 완료!" : "오답입니다. 출석 실패!");
        return "checkResult";
    }
}
