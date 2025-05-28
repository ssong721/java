package com.meetingjava.snowball.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

import com.meetingjava.snowball.service.UserService;
import com.meetingjava.snowball.entity.User;

@Controller
@SessionAttributes("name") // name을 세션에 저장
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
                        Model model) {
        try {
            User user = userService.login(username, password);
            model.addAttribute("name", user.getName());
            return "welcome";  // 로그인 성공 페이지
        } catch (Exception e) {
            model.addAttribute("error", "로그인 실패: " + e.getMessage());
            return "login";
        }
    }
    
    // 환영 페이지 보여주기 (로그인 후 리디렉션 등에서 사용)
   @GetMapping("/welcome")
    public String welcomePage(@ModelAttribute(value = "name", binding = false) String name, Model model) {
    if (name == null || name.isBlank()) {
        return "redirect:/login"; // 세션 없으면 로그인으로 돌려보냄
    }
    model.addAttribute("name", name);
        return "welcome";
    }


}

