package com.meetingjava.snowball.controller;

import com.meetingjava.snowball.dto.ScheduleMakeDto;
import com.meetingjava.snowball.entity.Schedule;
import com.meetingjava.snowball.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class ScheduleMakeController {

    private final ScheduleRepository scheduleRepository;

    @PostMapping("/schedulemake")
    public String createSchedule(@ModelAttribute ScheduleMakeDto scheduleDto) {
        // 1. DTO → Entity 변환 후 DB 저장
        Schedule schedule = scheduleDto.toEntity(); // 또는 직접 생성
        scheduleRepository.save(schedule);

        // 2. 완료 후 리다이렉트 또는 뷰 반환
        return "redirect:/dashboard"; // 대시보드로 다시 갈 수 있도록 반환
    }

    @GetMapping("/schedulemake")
    public String showForm() {
        return "schedulemake"; // templates/schedulemake.html 을 뜻함
    }
}
