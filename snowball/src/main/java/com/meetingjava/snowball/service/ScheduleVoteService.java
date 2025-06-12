package com.meetingjava.snowball.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetingjava.snowball.entity.ScheduleVote;
import com.meetingjava.snowball.entity.VoteSubmission;
import com.meetingjava.snowball.repository.ScheduleVoteRepository;
import com.meetingjava.snowball.repository.VoteSubmissionRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ScheduleVoteService {

    private final ScheduleVoteRepository voteRepository;
    private final VoteSubmissionRepository voteSubmissionRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ScheduleVote findByMeetingId(String meetingId) {
        return voteRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new NoSuchElementException("해당 meetingId에 대한 투표 없음: " + meetingId));
    }

    public ScheduleVote createVote(Date start, Date end, int durationMinutes, String meetingId) {
        ScheduleVote vote = new ScheduleVote(start, end, durationMinutes, meetingId);
        return voteRepository.save(vote);
    }

    public void startVoting(String voteId) {
        ScheduleVote vote = getVoteOrThrow(voteId);
        vote.startVoting();
        voteRepository.save(vote);
    }

    public void submitVote(String voteId, String user, List<String> selectedTimeStrs) {
        List<Date> selectedDates = convertToDateList(selectedTimeStrs);

        if (selectedDates.isEmpty()) {
            throw new IllegalArgumentException("❌ 유효한 시간 없음. 모든 시간 파싱 실패");
        }

        ScheduleVote vote = getVoteOrThrow(voteId);

        for (Date time : selectedDates) {
            if (time == null) continue;
            VoteSubmission submission = new VoteSubmission(vote, user, time);
            voteSubmissionRepository.save(submission);
        }

        vote.submitVote(user, selectedDates);
        voteRepository.save(vote);
    }

    private List<Date> convertToDateList(List<String> timeStrs) {
        List<Date> dates = new ArrayList<>();
        for (String timeStr : timeStrs) {
            try {
                Instant instant = Instant.parse(timeStr);
                dates.add(Date.from(instant));
            } catch (Exception e) {
                System.out.println("❌ 시간 변환 실패: " + timeStr);
            }
        }
        return dates;
    }

    public void closeVoting(String voteId) {
        ScheduleVote vote = getVoteOrThrow(voteId);
        vote.closeVoting();
        voteRepository.save(vote);
    }

    public String getVoteSummaryForGemini(String voteId) {
        List<VoteSubmission> submissions = voteSubmissionRepository.findByVote_VoteId(voteId);
        Map<String, List<Date>> voteMap = new HashMap<>();

        for (VoteSubmission submission : submissions) {
            voteMap.computeIfAbsent(submission.getUserName(), k -> new ArrayList<>())
                   .add(submission.getSelectedTime());
        }

        ScheduleVote vote = getVoteOrThrow(voteId);
        vote.setVotes(voteMap);

        return vote.prepareVoteDataForGPT();
    }

    public void confirmTime(String voteId, Date time) {
        ScheduleVote vote = getVoteOrThrow(voteId);
        vote.confirmTime(time);
        voteRepository.save(vote);
    }

    public ScheduleVote findById(String voteId) {
        return voteRepository.findById(voteId)
                .orElseThrow(() -> new NoSuchElementException("해당 voteId 없음: " + voteId));
    }

    public List<ScheduleVote> findAll() {
        return voteRepository.findAll();
    }

    private ScheduleVote getVoteOrThrow(String voteId) {
        return voteRepository.findById(voteId)
                .orElseThrow(() -> new NoSuchElementException("해당 voteId 없음: " + voteId));
    }

    public ScheduleVote getVote(String voteId) {
        return getVoteOrThrow(voteId);
    }

    public void updateRecommendedTime(String voteId, String recommendedTimeStr) {
        try {
            Instant instant = Instant.parse(recommendedTimeStr);
            Date parsedTime = Date.from(instant);

            ScheduleVote vote = getVoteOrThrow(voteId);
            vote.setRecommendedTime(parsedTime);
            voteRepository.save(vote);

            System.out.println("✅ 추천 시간 저장 완료: " + parsedTime);
        } catch (Exception e) {
            System.out.println("❌ 추천 시간 파싱 실패: " + recommendedTimeStr);
        }
    }

    public void recommendBestTime(String meetingId) {
        ScheduleVote vote = findByMeetingId(meetingId);
        String voteId = vote.getVoteId();

        List<VoteSubmission> submissions = voteSubmissionRepository.findByVote_VoteId(voteId);
        Map<Date, Integer> countMap = new HashMap<>();

        for (VoteSubmission submission : submissions) {
            Date time = submission.getSelectedTime();
            countMap.put(time, countMap.getOrDefault(time, 0) + 1);
        }

        Date bestTime = countMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (bestTime != null) {
            vote.setRecommendedTime(bestTime);

            List<String> availableUsers = submissions.stream()
                    .filter(s -> s.getSelectedTime().equals(bestTime))
                    .map(VoteSubmission::getUserName)
                    .distinct()
                    .toList();

            vote.setAvailableUsers(availableUsers);
            voteRepository.save(vote);
        }
    }

    public Map<String, Object> getRecommendedTimeInfo(String meetingId) {
        ScheduleVote vote = findByMeetingId(meetingId);
        String voteId = vote.getVoteId();

        List<VoteSubmission> submissions = voteSubmissionRepository.findByVote_VoteId(voteId);
        Map<Date, Integer> countMap = new HashMap<>();

        for (VoteSubmission submission : submissions) {
            Date time = submission.getSelectedTime();
            countMap.put(time, countMap.getOrDefault(time, 0) + 1);
        }

        Date bestTime = countMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        List<String> availableUsers = submissions.stream()
                .filter(s -> s.getSelectedTime().equals(bestTime))
                .map(VoteSubmission::getUserName)
                .distinct()
                .toList();

        Map<String, Object> result = new HashMap<>();
        result.put("recommendedTime", bestTime);
        result.put("availableUsers", availableUsers);
        return result;
    }
}
