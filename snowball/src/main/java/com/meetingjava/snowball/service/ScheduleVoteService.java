package com.meetingjava.snowball.service;

import com.meetingjava.snowball.entity.ScheduleVote;

import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class ScheduleVoteService {
    private final Map<String, ScheduleVote> voteMap = new HashMap<>();

    public ScheduleVote createVote(Date start, Date end, int duration, boolean isRecurring) {
        ScheduleVote vote = new ScheduleVote(start, end, duration, isRecurring);
        voteMap.put(vote.getVoteId(), vote);
        return vote;
    }

    public void startVoting(String voteId) {
        voteMap.get(voteId).startVoting();
    }

    public void submitVote(String voteId, String user, List<Date> selectedTimes) {
        voteMap.get(voteId).submitVote(user, selectedTimes);
    }

    public void closeVoting(String voteId) {
        voteMap.get(voteId).closeVoting();
    }

    public void recommendUsingGPT(String voteId) {
        ScheduleVote vote = voteMap.get(voteId);
        String json = vote.prepareVoteDataForGPT();

        // GPT에 API 요청 (간단한 예시, 실제 API 연동 필요)
        List<Date> dummyResponse = new ArrayList<>();
        dummyResponse.add(new Date(System.currentTimeMillis() + 3600000)); // +1시간
        dummyResponse.add(new Date(System.currentTimeMillis() + 7200000)); // +2시간

        vote.parseGPTResponse(dummyResponse);
    }

    public void confirmTime(String voteId, Date time) {
        voteMap.get(voteId).confirmTime(time);
    }

    public ScheduleVote getVote(String voteId) {
        return voteMap.get(voteId);
    }
}
