package com.meetingjava.snowball.controller;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

import com.meetingjava.snowball.service.UserService;

import jakarta.servlet.http.HttpSession;

import com.meetingjava.snowball.dto.HomeDto;
import com.meetingjava.snowball.entity.User;

@Controller
public class UserPageController {

    private final UserService userService;

    public UserPageController(UserService userService) {
        this.userService = userService;
    }

    // 회원가입 폼 보여주기
    @GetMapping("/signup")
    public String signupForm() {
        return "signup";  // src/main/resources/templates/signup.html
    }

    // 회원가입 처리
    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                     @RequestParam String password,
                     @RequestParam String name,
                     Model model,
                     RedirectAttributes redirectAttributes) {
        try {
            userService.registerUser(username, password, name);
            // 여기! flash attribute로 팝업용 메시지 전달
            redirectAttributes.addFlashAttribute("signupSuccess", true);
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "회원가입 실패: " + e.getMessage());
            return "signup";
        }
    }


    // 로그인 폼 보여주기
    @GetMapping("/login")
    public String loginForm() {
        return "login";  // login.html
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        Model model,
                        HttpSession session) {
        try {
            User user = userService.login(username, password);
            session.setAttribute("loginUser", user); // ← 세션에 저장!
            model.addAttribute("name", user.getName());
            return "redirect:/home";  // 로그인 성공 후 홈으로 이동
        } catch (Exception e) {
            model.addAttribute("error", "로그인 실패: " + e.getMessage());
            return "login";
        }
    }
    
}

