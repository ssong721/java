package com.meetingjava.snowball.controller;

import com.meetingjava.snowball.entity.ScheduleVote;
import com.meetingjava.snowball.service.ScheduleVoteService;;

import java.util.*;

@RestController
@RequestMapping("/api/schedule-vote")
public class ScheduleVoteController {

    private final ScheduleVoteService voteService;

    public ScheduleVoteController(ScheduleVoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping("/create")
    public ScheduleVote create(@RequestBody VoteRequest request) {
        return voteService.createVote(request.startTime, request.endTime, request.durationMinutes, request.isRecurring);
    }

    @PostMapping("/{voteId}/start")
    public void start(@PathVariable String voteId) {
        voteService.startVoting(voteId);
    }

    @PostMapping("/{voteId}/vote")
    public void vote(@PathVariable String voteId, @RequestBody VoteSubmit submit) {
        voteService.submitVote(voteId, submit.user, submit.selectedTimes);
    }

    @PostMapping("/{voteId}/close")
    public void close(@PathVariable String voteId) {
        voteService.closeVoting(voteId);
    }

    @PostMapping("/{voteId}/gpt")
    public void requestGPT(@PathVariable String voteId) {
        voteService.recommendUsingGPT(voteId);
    }

    @PostMapping("/{voteId}/confirm")
    public void confirm(@PathVariable String voteId, @RequestParam long timestamp) {
        voteService.confirmTime(voteId, new Date(timestamp));
    }


    static class VoteRequest {
        public Date startTime;
        public Date endTime;
        public int durationMinutes;
        public boolean isRecurring;
    }

    static class VoteSubmit {
        public String user;
        public List<Date> selectedTimes;
    }
}
