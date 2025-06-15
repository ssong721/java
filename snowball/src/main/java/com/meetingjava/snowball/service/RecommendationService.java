package com.meetingjava.snowball.service;

import com.meetingjava.snowball.entity.ScheduleVote;
import com.meetingjava.snowball.entity.VoteSubmission;
import com.meetingjava.snowball.repository.VoteSubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final VoteSubmissionRepository voteSubmissionRepository;
    private final ScheduleVoteService scheduleVoteService; // ✅ voteId 얻기 위해 필요

    public Map<String, Object> getRecommendedInfo(String meetingId) {
        ScheduleVote vote = scheduleVoteService.findByMeetingId(meetingId); // ✅ voteId 조회
        String voteId = vote.getVoteId();

        List<VoteSubmission> submissions = voteSubmissionRepository.findByVote_VoteId(voteId); // ✅ 핵심 수정
        System.out.println("📊 투표 개수: " + submissions.size());

        Map<Date, Integer> countMap = new HashMap<>();
        for (VoteSubmission submission : submissions) {
            System.out.println("👤 " + submission.getUserName() + " → " + submission.getSelectedTime());
            Date time = submission.getSelectedTime();
            countMap.put(time, countMap.getOrDefault(time, 0) + 1);
        }

        Date bestTime = countMap.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);

        System.out.println("✅ 추천된 시간: " + bestTime);

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
