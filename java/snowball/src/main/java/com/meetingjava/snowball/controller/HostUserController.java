package com.smu.snowball.controller;

import com.smu.snowball.entity.HostUser;
import com.smu.snowball.repository.HostUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hostuser")
public class HostUserController {

    @Autowired
    private HostUserRepository hostUserRepository;

    // 전체 모임장 목록 조회
    @GetMapping
    public List<HostUser> getAllHostUsers() {
        return hostUserRepository.findAll();
    }

    // 모임장 등록
    @PostMapping
    public HostUser createHostUser(@RequestBody HostUser hostUser) {
        return hostUserRepository.save(hostUser);
    }
}
