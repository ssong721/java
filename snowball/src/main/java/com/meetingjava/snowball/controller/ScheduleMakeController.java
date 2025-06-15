package com.meetingjava.snowball.controller;

import com.meetingjava.snowball.dto.ScheduleMakeDto;
import com.meetingjava.snowball.entity.Schedule;
import com.meetingjava.snowball.entity.ScheduleCandidate;
import com.meetingjava.snowball.repository.ScheduleCandidateRepository;
import com.meetingjava.snowball.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequiredArgsConstructor
public class ScheduleMakeController {

    private final ScheduleCandidateRepository scheduleCandidateRepository; // ✅ 0615 준서 수정

    @PostMapping("/schedulemake")
    public String createSchedule(@ModelAttribute ScheduleMakeDto scheduleDto) {
        // ✅ 1. 전달된 값 제대로 왔는지 로그 찍기
        System.out.println("🚀 컨트롤러 도착!");
        System.out.println("📌 scheduleName: " + scheduleDto.getScheduleName());
        System.out.println("📌 startDate: " + scheduleDto.getStartDate());
        System.out.println("📌 endDate: " + scheduleDto.getEndDate());

        // ✅ 2. Entity 변환 → 이 과정에서 예외 날 수 있음
        ScheduleCandidate candidate = scheduleDto.toCandidateEntity(); // ✅ 0615 준서 수정
        candidate.setMeetingId(scheduleDto.getMeetingId()); // 0615 준서 수정

        // ✅ 3. DB 저장
        scheduleCandidateRepository.save(candidate); // ✅ 0615 준서 수정

        // ✅ 4. 정상 완료 시 리다이렉트
        return "redirect:/dashboard/" + scheduleDto.getMeetingId();
    }

    @GetMapping("/schedulemake")
    public String showForm(@RequestParam String meetingId, Model model) {
        model.addAttribute("meetingId", meetingId);
        return "schedulemake"; // → templates/schedulemake.html
    }
}
