package com.meetingjava.snowball.entity;

import jakarta.persistence.*;
import java.util.*;

@Entity
public class ScheduleVote {

    @Id
    @Column(name = "vote_id")  // 🔥 반드시 추가해야 함
    private String voteId;

    private String meetingId;
    private Date startTime;
    private Date endTime;
    private int durationMinutes;
    private Date confirmedTime;
    private boolean isVotingClosed;

    private Date recommendedTime;  // ✅ Gemini 추천 시간
        public void setRecommendedTime(Date time) {
        this.recommendedTime = time;
    }

    @Transient
    private List<String> availableUsers = new ArrayList<>();

    @Transient
    private Map<String, List<Date>> votes = new HashMap<>(); // ✅ DB에 저장하지 않고 메모리에서만 사용

    @Transient
    private List<Date> aiRecommendedSlots = new ArrayList<>(); // Gemini 응답 전체 보관

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

    // ✅ 투표 제출 (메모리 구조에만 추가 — DB는 VoteSubmission에 저장됨)
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

    // ✅ Getters & Setters

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

    public void setVotes(Map<String, List<Date>> votes) {
        this.votes = votes;
    }

    public void setAvailableUsers(List<String> availableUsers) {
        this.availableUsers = availableUsers;
    }

    // ✅ 수동 설정을 위한 Setter 추가 (스케줄 생성 시 연동)
    public void setVoteId(String voteId) {
        this.voteId = voteId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }
}
