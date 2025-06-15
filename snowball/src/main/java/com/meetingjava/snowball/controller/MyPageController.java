package com.meetingjava.snowball.controller;

import com.meetingjava.snowball.dto.LogoutRequest;
import com.meetingjava.snowball.dto.MemberDeleteRequest;
import com.meetingjava.snowball.service.UserService;
import com.meetingjava.snowball.entity.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

        // 세션에서 사용자 정보를 가져와 이름만 전달
        if (loginUser != null) {
            model.addAttribute("username", loginUser.getUsername());
            model.addAttribute("name", loginUser.getName());
            model.addAttribute("userId", loginUser.getId());
        }

        return "mypage"; // 마이페이지 뷰로 이동
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 무효화
        return "redirect:/login?logout"; // 로그인 페이지로 리디렉션
    }

    @PostMapping("/delete")
    public String deleteUser(Model model, 
                        @AuthenticationPrincipal UserDetails userDetails,
                        HttpSession session) {
        String username = userDetails.getUsername();

            // 이름 정보도 같이 담기 (세션에서 가져오거나 UserDetails에서 직접 가져오기)
            User loginUser = (User) session.getAttribute("loginUser");
            if (loginUser != null) {
                model.addAttribute("username", loginUser.getName());
            } else {
                model.addAttribute("username", username); // fallback
            }

        try {
            userService.deleteUserById(loginUser.getId());
            session.invalidate(); // 세션 만료 (로그아웃)
            return "redirect:/login?deleted"; // 로그인 페이지로 리디렉션
        } catch (IllegalArgumentException e) {
            // 삭제할 수 없는 경우 (예: 존재하지 않는 사용자)
            return "redirect:/mypage?error=" + e.getMessage();
        } catch (Exception e) {
            // 기타 오류 처리
            return "redirect:/mypage?error=server";
        }
    }
}
