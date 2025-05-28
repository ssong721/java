package com.meetingjava.snowball.controller;

import com.meetingjava.snowball.dto.ScheduleVoteRequest;
import com.meetingjava.snowball.entity.ScheduleVote;
import com.meetingjava.snowball.service.ScheduleVoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/api/schedule-vote")
@RequiredArgsConstructor
public class ScheduleVoteController {

    private final ScheduleVoteService voteService;

    /**
     * 일정 투표 생성
     */
    @PostMapping("/create")
    public ResponseEntity<ScheduleVote> createVote(@RequestBody ScheduleVoteRequest request) throws ParseException {
        // 날짜 + 시간 문자열을 Date 객체로 파싱
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date start = format.parse(request.getStartDate() + " " + request.getStartTime()); // ex: 2025-06-24 10:00
        Date end = format.parse(request.getEndDate() + " " + request.getEndTime()); // ex: 2025-06-27 22:00

        // 서비스에 전달
        ScheduleVote createdVote = voteService.createVote(
                start,
                end,
                request.getDurationMinutes(),
                request.getMeetingId());

        return ResponseEntity.ok(createdVote);
    }

    /**
     * 투표 시작
     */
    @PostMapping("/{voteId}/start")
    public ResponseEntity<Void> startVote(@PathVariable String voteId) {
        voteService.startVoting(voteId);
        return ResponseEntity.ok().build();
    }

    /**
     * 투표 제출
     */
    @PostMapping("/{voteId}/vote")
    public ResponseEntity<Void> submitVote(@PathVariable String voteId, @RequestBody VoteSubmit request) {
        voteService.submitVote(voteId, request.getUser(), request.getSelectedTimes());
        return ResponseEntity.ok().build();
    }

    /**
     * 투표 마감
     */
    @PostMapping("/{voteId}/close")
    public ResponseEntity<Void> closeVote(@PathVariable String voteId) {
        voteService.closeVoting(voteId);
        return ResponseEntity.ok().build();
    }

    /**
     * Gemini 추천 요청
     */
    @PostMapping("/{voteId}/gpt")
    public ResponseEntity<Void> requestGPT(@PathVariable String voteId) {
        voteService.recommendUsingGemini(voteId); // ✅ 메서드 이름 수정
        return ResponseEntity.ok().build();
    }

    /**
     * 최종 시간 확정
     */
    @PostMapping("/{voteId}/confirm")
    public ResponseEntity<Void> confirmTime(@PathVariable String voteId, @RequestParam long timestamp) {
        voteService.confirmTime(voteId, new Date(timestamp));
        return ResponseEntity.ok().build();
    }

    // 사용자 투표 제출용 내부 클래스
    static class VoteSubmit {
        private String user;
        private java.util.List<Date> selectedTimes;

        public String getUser() {
            return user;
        }

        public java.util.List<Date> getSelectedTimes() {
            return selectedTimes;
        }
    }
}
