package com.meetingjava.snowball.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ScheduleVote {

    private final String voteId;
    private Date startTime;
    private Date endTime;
    private int durationMinutes;
    private Map<String, List<Date>> votes;
    private List<Date> aiRecommendedSlots;
    private Date confirmedTime;
    private boolean isVotingClosed;
    private boolean isRecurring;

    public ScheduleVote(Date startTime, Date endTime, int durationMinutes, boolean isRecurring) {
        this.voteId = UUID.randomUUID().toString();
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMinutes = durationMinutes;
        this.votes = new HashMap<>();
        this.aiRecommendedSlots = new ArrayList<>();
        this.isVotingClosed = false;
        this.isRecurring = isRecurring;
    }

    public void startVoting() {
        this.isVotingClosed = false;
    }

    public void submitVote(String userName, List<Date> selectedTimes) {
        if (!isVotingClosed) {
            votes.put(userName, selectedTimes);
        }
    }

    public void closeVoting() {
        this.isVotingClosed = true;
    }

    public String prepareVoteDataForGPT() {
        StringBuilder sb = new StringBuilder("{ \"votes\": [");
        int i = 0;
        for (Map.Entry<String, List<Date>> entry : votes.entrySet()) {
            sb.append("{ \"user\": \"").append(entry.getKey()).append("\", \"times\": [");
            for (int j = 0; j < entry.getValue().size(); j++) {
                sb.append("\"").append(entry.getValue().get(j)).append("\"");
                if (j < entry.getValue().size() - 1) sb.append(", ");
            }
            sb.append("]}");
            if (i < votes.size() - 1) sb.append(", ");
            i++;
        }
        sb.append("], \"durationMinutes\": ").append(durationMinutes).append(" }");
        return sb.toString();
    }

    public void parseGPTResponse(List<Date> recommended) {
        this.aiRecommendedSlots = recommended;
    }

    public void confirmTime(Date time) {
        this.confirmedTime = time;
    }


    public String getVoteId() { return voteId; }
    public List<Date> getAiRecommendedSlots() { return aiRecommendedSlots; }
    public boolean isVotingClosed() { return isVotingClosed; }
    public Date getConfirmedTime() { return confirmedTime; }
}
