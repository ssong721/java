package com.meetingjava.snowball.controller;

import org.springframework.web.bind.annotation.*;
import com.meetingjava.snowball.service.*;
import com.meetingjava.snowball.entity.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public String signup(@RequestBody SignupRequest request) {
        userService.registerUser(request.getUsername(), request.getPassword(), request.getName());
        return "회원가입 성공";
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        userService.login(request.getUsername(), request.getPassword());
        return "로그인 성공";
    }
}
