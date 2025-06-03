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

    /**
     * 일정 투표 생성
     */
    @PostMapping("/create")
    public ResponseEntity<ScheduleVote> createVote(@RequestBody ScheduleVoteRequest request) throws ParseException {
        // 날짜 + 시간 문자열을 Date 객체로 파싱
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
    public ResponseEntity<String> requestGPT(@PathVariable String voteId) {
        // 1. voteId로 투표 데이터 조회 (예시로 문자열 구성)
        String voteSummary = voteService.getVoteSummaryForGemini(voteId);

        // 2. Gemini에 추천 시간 요청
        String recommendedTime = geminiService.getRecommendedTime(voteSummary);

        // 3. 결과를 문자열로 응답
        return ResponseEntity.ok(recommendedTime);
    }

    /**
     * 최종 시간 확정
     */
    @PostMapping("/{voteId}/confirm")
    public ResponseEntity<Void> confirmTime(@PathVariable String voteId, @RequestParam long timestamp) {
        voteService.confirmTime(voteId, new Date(timestamp));
        return ResponseEntity.ok().build();
    }

    /**
     * ✅ [추가된 부분] voteId로 단일 투표 조회 (JSON 확인용)
     */
    @GetMapping("/{voteId}")
    public ResponseEntity<ScheduleVote> getVoteById(@PathVariable String voteId) {
        ScheduleVote vote = voteService.findById(voteId);
        return ResponseEntity.ok(vote);
    }

    /**
     * ✅ [선택 사항] 전체 투표 목록 보기 (voteId들 확인용)
     */
    @GetMapping("/all")
    public ResponseEntity<List<ScheduleVote>> getAllVotes() {
        return ResponseEntity.ok(voteService.findAll());
    }

    // 사용자 투표 제출용 내부 클래스
    static class VoteSubmit {
        private String user;
        private List<Date> selectedTimes;

        public String getUser() {
            return user;
        }

        public List<Date> getSelectedTimes() {
            return selectedTimes;
        }
    }
}
