package com.meetingjava.snowball.controller;

import com.meetingjava.snowball.dto.ScheduleVoteRequest;
import com.meetingjava.snowball.entity.ScheduleVote;
import com.meetingjava.snowball.service.GeminiService;
import com.meetingjava.snowball.service.ScheduleVoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/schedule-vote")
@RequiredArgsConstructor
public class ScheduleVoteController {

    private final ScheduleVoteService voteService;
    private final GeminiService geminiService;

    // ✅ 투표 생성
    @PostMapping("/create")
    public ResponseEntity<ScheduleVote> createVote(@RequestBody ScheduleVoteRequest request) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date start = format.parse(request.getStartDate() + " " + request.getStartTime());
        Date end = format.parse(request.getEndDate() + " " + request.getEndTime());

        ScheduleVote createdVote = voteService.createVote(
                start,
                end,
                request.getDurationMinutes(),
                request.getMeetingId());

        return ResponseEntity.ok(createdVote);
    }

    // ✅ 투표 시작
    @PostMapping("/{voteId}/start")
    public ResponseEntity<Void> startVote(@PathVariable String voteId) {
        voteService.startVoting(voteId);
        return ResponseEntity.ok().build();
    }

    // ✅ 투표 제출 (프론트에서 오는 selectedTimes는 문자열 리스트임)
    @PostMapping("/{voteId}/vote")
    public ResponseEntity<Void> submitVote(@PathVariable String voteId, @RequestBody VoteSubmit request) {
        voteService.submitVote(voteId, request.getUser(), request.getSelectedTimes());
        return ResponseEntity.ok().build();
    }

    // ✅ 투표 마감
    @PostMapping("/{voteId}/close")
    public ResponseEntity<Void> closeVote(@PathVariable String voteId) {
        voteService.closeVoting(voteId);
        return ResponseEntity.ok().build();
    }

    // ✅ Gemini 추천 요청
    @PostMapping("/{voteId}/gpt")
    public ResponseEntity<String> requestGPT(@PathVariable String voteId) {
        String voteSummary = voteService.getVoteSummaryForGemini(voteId);
        String recommendedTime = geminiService.getRecommendedTime(voteSummary);

        if (recommendedTime == null) {
            return ResponseEntity.internalServerError().body("❌ Gemini 추천 실패");
        }

        voteService.updateRecommendedTime(voteId, recommendedTime); // ✅ DB에 저장
        return ResponseEntity.ok("✅ 추천 완료");
    }

    // ✅ 시간 확정
    @PostMapping("/{voteId}/confirm")
    public ResponseEntity<Void> confirmTime(@PathVariable String voteId, @RequestParam long timestamp) {
        voteService.confirmTime(voteId, new Date(timestamp));
        return ResponseEntity.ok().build();
    }

    // ✅ 단일 투표 조회
    @GetMapping("/{voteId}")
    public ResponseEntity<ScheduleVote> getVoteById(@PathVariable String voteId) {
        ScheduleVote vote = voteService.findById(voteId);
        return ResponseEntity.ok(vote);
    }

    // ✅ 전체 투표 목록 조회
    @GetMapping("/all")
    public ResponseEntity<List<ScheduleVote>> getAllVotes() {
        return ResponseEntity.ok(voteService.findAll());
    }

    // ✅ meetingId로 투표 조회
    @GetMapping("/by-meeting/{meetingId}")
    public ResponseEntity<ScheduleVote> getVoteByMeetingId(@PathVariable String meetingId) {
        ScheduleVote vote = voteService.findByMeetingId(meetingId);
        return ResponseEntity.ok(vote);
    }

    // ✅ 내부 클래스: 프론트에서 받는 투표 요청 형식
    static class VoteSubmit {
        private String user;
        private List<String> selectedTimes;

        public String getUser() {
            return user;
        }

        public List<String> getSelectedTimes() {
            return selectedTimes;
        }
    }
}
