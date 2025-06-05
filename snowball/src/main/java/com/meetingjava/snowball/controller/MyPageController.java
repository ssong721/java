package com.meetingjava.snowball.controller;

import com.meetingjava.snowball.dto.LogoutRequest;
import com.meetingjava.snowball.dto.MemberDeleteRequest;
import com.meetingjava.snowball.service.UserService;
import com.meetingjava.snowball.entity.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MyPageController {

    private final UserService userService;

    public MyPageController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/mypage")
    public String myPage(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("username", loginUser.getUsername());
        model.addAttribute("name", loginUser.getName());
        return "mypage";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 무효화
        return "redirect:/login?logout"; // 로그인 페이지로 리디렉션
    }

    @PostMapping("/delete-account")
    public String deleteAccount(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        try {
            userService.deleteByUsername(loginUser.getUsername());
            session.invalidate(); // 세션 만료
            return "redirect:/login?deleted";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "mypage";
        }
    }

}
