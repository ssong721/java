package com.meetingjava.snowball.controller;

import com.meetingjava.snowball.dto.ScheduleMakeDto;
import com.meetingjava.snowball.entity.ScheduleCandidate;
import com.meetingjava.snowball.entity.ScheduleVote;
import com.meetingjava.snowball.repository.ScheduleCandidateRepository;
import com.meetingjava.snowball.repository.ScheduleRepository;
import com.meetingjava.snowball.repository.ScheduleVoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequiredArgsConstructor
public class ScheduleMakeController {

    private final ScheduleCandidateRepository scheduleCandidateRepository;
    private final ScheduleVoteRepository voteRepository;

    @PostMapping("/schedulemake")
    public String createSchedule(@ModelAttribute ScheduleMakeDto scheduleDto) {
        System.out.println("🚀 컨트롤러 도착!");
        System.out.println("📌 scheduleName: " + scheduleDto.getScheduleName());
        System.out.println("📌 startDate: " + scheduleDto.getStartDate());
        System.out.println("📌 endDate: " + scheduleDto.getEndDate());

        // ✅ DTO → Entity 변환
        ScheduleCandidate candidate = scheduleDto.toCandidateEntity();
        candidate.setMeetingId(scheduleDto.getMeetingId());

        // ✅ 후보 일정 저장
        scheduleCandidateRepository.save(candidate);

        // ✅ ScheduleVote도 같은 voteId로 저장
        ScheduleVote vote = new ScheduleVote();
        vote.setVoteId(candidate.getVoteId());
        vote.setMeetingId(candidate.getMeetingId());
        voteRepository.save(vote);

        return "redirect:/dashboard/" + scheduleDto.getMeetingId();
    }

    @GetMapping("/schedulemake")
    public String showForm(@RequestParam String meetingId, Model model) {
        model.addAttribute("meetingId", meetingId);
        return "schedulemake"; // → templates/schedulemake.html
    }
}
