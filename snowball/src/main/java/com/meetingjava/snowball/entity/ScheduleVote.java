package com.meetingjava.snowball.entity;

import jakarta.persistence.*;
import java.util.*;

@Entity
public class ScheduleVote {

    @Id
    private String voteId;

    private Long meetingId;
    private Date startTime;
    private Date endTime;
    private int durationMinutes;
    private Date confirmedTime;
    private boolean isVotingClosed;

    // 👉 JPA가 저장 불가한 필드는 임시로 제외
    @Transient
    private Map<String, List<Date>> votes = new HashMap<>();

    @Transient
    private List<Date> aiRecommendedSlots = new ArrayList<>();

    // ✅ 기본 생성자 (JPA용)
    public ScheduleVote() {
    }

    // ✅ 커스텀 생성자
    public ScheduleVote(Date startTime, Date endTime, int durationMinutes, Long meetingId) {
        this.voteId = UUID.randomUUID().toString();
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMinutes = durationMinutes;
        this.isVotingClosed = false;
        this.meetingId = meetingId;
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
                if (j < entry.getValue().size() - 1)
                    sb.append(", ");
            }
            sb.append("]}");
            if (i < votes.size() - 1)
                sb.append(", ");
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

    // ✅ Getter들
    public String getVoteId() {
        return voteId;
    }

    public Long getMeetingId() {
        return meetingId;
    }

    public List<Date> getAiRecommendedSlots() {
        return aiRecommendedSlots;
    }

    public boolean isVotingClosed() {
        return isVotingClosed;
    }

    public Date getConfirmedTime() {
        return confirmedTime;
    }
}
