package com.meetingjava.snowball.entity;

import jakarta.persistence.*;
import java.util.*;

@Entity
public class ScheduleVote {

    @Id
    private String voteId;

    private String meetingId;
    private Date startTime;
    private Date endTime;
    private int durationMinutes;
    private Date confirmedTime;
    private boolean isVotingClosed;

    private Date recommendedTime;  // ✅ AI 추천 시간

    @ElementCollection
    private List<String> availableUsers = new ArrayList<>(); // ✅ 가능한 인원

    // ✅ 투표 내역을 DB에 저장하도록 변경
    @ElementCollection
    @CollectionTable(name = "vote_submission", joinColumns = @JoinColumn(name = "vote_id"))
    @MapKeyColumn(name = "user_name")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "selected_time")
    private Map<String, List<Date>> votes = new HashMap<>();

    @Transient
    private List<Date> aiRecommendedSlots = new ArrayList<>();

    // ✅ 기본 생성자
    public ScheduleVote() {
    }

    // ✅ 생성자
    public ScheduleVote(Date startTime, Date endTime, int durationMinutes, String meetingId) {
        this.voteId = UUID.randomUUID().toString();
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMinutes = durationMinutes;
        this.isVotingClosed = false;
        this.meetingId = meetingId;
    }

    // ✅ 투표 시작
    public void startVoting() {
        this.isVotingClosed = false;
    }

    // ✅ 투표 제출
    public void submitVote(String userName, List<Date> selectedTimes) {
        if (!isVotingClosed) {
            votes.put(userName, selectedTimes);
        }
    }

    // ✅ 투표 마감
    public void closeVoting() {
        this.isVotingClosed = true;
    }

    // ✅ Gemini에 보낼 JSON 구성
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

    // ✅ Gemini 응답 파싱 + 추천 시간 저장
    public void parseGPTResponse(List<Date> recommended) {
        this.aiRecommendedSlots = recommended;
        if (!recommended.isEmpty()) {
            this.recommendedTime = recommended.get(0);
        }
    }

    // ✅ 추천 시간 기준 가능한 유저 필터링
    public void calculateAvailableUsers() {
        if (recommendedTime == null || votes == null) return;

        List<String> result = new ArrayList<>();
        for (Map.Entry<String, List<Date>> entry : votes.entrySet()) {
            if (entry.getValue().contains(recommendedTime)) {
                result.add(entry.getKey());
            }
        }
        this.availableUsers = result;
    }

    // ✅ 시간 확정
    public void confirmTime(Date time) {
        this.confirmedTime = time;
    }

    // ✅ Getters

    public String getVoteId() {
        return voteId;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public boolean isVotingClosed() {
        return isVotingClosed;
    }

    public Date getConfirmedTime() {
        return confirmedTime;
    }

    public Date getRecommendedTime() {
        return recommendedTime;
    }

    public List<String> getAvailableUsers() {
        return availableUsers;
    }

    public List<Date> getAiRecommendedSlots() {
        return aiRecommendedSlots;
    }

    public Map<String, List<Date>> getVotes() {
        return votes;
    }
}
