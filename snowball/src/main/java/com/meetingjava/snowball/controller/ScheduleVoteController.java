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

    @PostMapping("/{voteId}/start")
    public ResponseEntity<Void> startVote(@PathVariable String voteId) {
        voteService.startVoting(voteId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{voteId}/vote")
    public ResponseEntity<Void> submitVote(@PathVariable String voteId, @RequestBody VoteSubmit request) {
        voteService.submitVote(voteId, request.getUser(), request.getSelectedTimes());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{voteId}/close")
    public ResponseEntity<Void> closeVote(@PathVariable String voteId) {
        voteService.closeVoting(voteId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{voteId}/gpt")
    public ResponseEntity<String> requestGPT(@PathVariable String voteId) {
        String voteSummary = voteService.getVoteSummaryForGemini(voteId);
        String recommendedTime = geminiService.getRecommendedTime(voteSummary);
        return ResponseEntity.ok(recommendedTime);
    }

    @PostMapping("/{voteId}/confirm")
    public ResponseEntity<Void> confirmTime(@PathVariable String voteId, @RequestParam long timestamp) {
        voteService.confirmTime(voteId, new Date(timestamp));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{voteId}")
    public ResponseEntity<ScheduleVote> getVoteById(@PathVariable String voteId) {
        ScheduleVote vote = voteService.findById(voteId);
        return ResponseEntity.ok(vote);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ScheduleVote>> getAllVotes() {
        return ResponseEntity.ok(voteService.findAll());
    }

    /**
     * ✅ 추가된 부분: meetingId로 vote 조회
     */
    @GetMapping("/by-meeting/{meetingId}")
    public ResponseEntity<ScheduleVote> getVoteByMeetingId(@PathVariable String meetingId) {
        ScheduleVote vote = voteService.findByMeetingId(meetingId);
        return ResponseEntity.ok(vote);
    }

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
