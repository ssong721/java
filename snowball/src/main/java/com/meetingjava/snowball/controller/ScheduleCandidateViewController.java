package com.meetingjava.snowball.controller;

import com.meetingjava.snowball.entity.ScheduleCandidate;
import com.meetingjava.snowball.entity.ScheduleVote;
import com.meetingjava.snowball.service.MeetingService;
import com.meetingjava.snowball.service.ScheduleVoteService;
import com.meetingjava.snowball.repository.ScheduleCandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/schedule/candidates")
public class ScheduleCandidateViewController {

    private final ScheduleCandidateRepository scheduleCandidateRepository;
    private final MeetingService meetingService;
    private final ScheduleVoteService scheduleVoteService;


    @GetMapping("/{meetingId}")
    public String showCandidateList(@PathVariable String meetingId, Model model) {
        List<ScheduleCandidate> candidates = scheduleCandidateRepository.findByMeetingId(meetingId);
        String meetingName = meetingService.getMeetingNameById(meetingId);

        model.addAttribute("meetingId", meetingId);
        model.addAttribute("meetingName", meetingName);
        model.addAttribute("candidates", candidates);

        return "schedulecandidates";
    }

    @GetMapping("/schedulevote/from-candidate/{candidateId}")
    public String redirectToVoteFromCandidate(@PathVariable Long candidateId) {
        // 1. 후보 일정 ID로 ScheduleCandidate 조회
        ScheduleCandidate candidate = scheduleCandidateRepository.findById(candidateId)
                .orElseThrow(() -> new NoSuchElementException("후보를 찾을 수 없습니다"));

        // 2. 후보 일정에 연결된 meetingId 가져오기
        String meetingId = candidate.getMeetingId();

        // 3. meetingId로 ScheduleVote 조회 (voteId 얻기 위해!)
        ScheduleVote vote = scheduleVoteService.findByMeetingId(meetingId);

        // 4. voteId를 이용해 리디렉션
        return "redirect:/schedulevote/" + vote.getVoteId();  // voteId로 리다이렉트
    }
}
