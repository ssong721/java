package com.meetingjava.snowball.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetingjava.snowball.entity.ScheduleVote;
import com.meetingjava.snowball.repository.ScheduleVoteRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ScheduleVoteService {

    private final ScheduleVoteRepository voteRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ScheduleVote findByMeetingId(String meetingId) {
        return voteRepository.findByMeetingId(meetingId)
            .orElseThrow(() -> new NoSuchElementException("해당 meetingId에 대한 투표 없음: " + meetingId));
    }

    // ✅ 투표 생성
    public ScheduleVote createVote(Date start, Date end, int durationMinutes, String meetingId) {
        ScheduleVote vote = new ScheduleVote(start, end, durationMinutes, meetingId);
        return voteRepository.save(vote);
    }

    // ✅ 투표 시작
    public void startVoting(String voteId) {
        ScheduleVote vote = getVoteOrThrow(voteId);
        vote.startVoting();
        voteRepository.save(vote);
    }

    // ✅ 투표 제출
    public void submitVote(String voteId, String user, List<Date> selectedTimes) {
        ScheduleVote vote = getVoteOrThrow(voteId);
        vote.submitVote(user, selectedTimes);
        voteRepository.save(vote);
    }

    // ✅ 투표 마감
    public void closeVoting(String voteId) {
        ScheduleVote vote = getVoteOrThrow(voteId);
        vote.closeVoting();
        voteRepository.save(vote);
    }

    // ✅ Gemini API 요청용 JSON 요약
    public String getVoteSummaryForGemini(String voteId) {
        ScheduleVote vote = getVoteOrThrow(voteId);
        return vote.prepareVoteDataForGPT(); // 엔티티 내부 메서드
    }

    // ✅ Gemini API 연동
    public void recommendUsingGemini(String voteId) {
        ScheduleVote vote = getVoteOrThrow(voteId);

        try {
            String geminiApiUrl = "https://your-gemini-api.com/recommend"; // TODO: 실제 API URL로 변경
            String jsonBody = vote.prepareVoteDataForGPT();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer YOUR_GEMINI_API_KEY"); // TODO: 실제 키로 변경

            HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(geminiApiUrl, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                List<Date> recommendedSlots = objectMapper.readValue(
                        response.getBody(),
                        new TypeReference<List<Date>>() {
                        });
                vote.parseGPTResponse(recommendedSlots);
                vote.calculateAvailableUsers();
                voteRepository.save(vote);
            } else {
                System.out.println("Gemini 호출 실패: " + response.getStatusCode());
            }

        } catch (Exception e) {
            System.out.println("Gemini 연동 중 예외 발생: " + e.getMessage());
        }
    }

    // ✅ 시간 확정
    public void confirmTime(String voteId, Date time) {
        ScheduleVote vote = getVoteOrThrow(voteId);
        vote.confirmTime(time);
        voteRepository.save(vote);
    }

    // ✅ 단일 조회
    public ScheduleVote findById(String voteId) {
        return voteRepository.findById(voteId)
                .orElseThrow(() -> new NoSuchElementException("해당 voteId 없음: " + voteId));
    }

    // ✅ 전체 목록
    public List<ScheduleVote> findAll() {
        return voteRepository.findAll();
    }

    // ✅ 내부 공통 조회 메서드
    private ScheduleVote getVoteOrThrow(String voteId) {
        return voteRepository.findById(voteId)
                .orElseThrow(() -> new NoSuchElementException("해당 voteId 없음: " + voteId));
    }

    // ✅ 직관적 단일 조회
    public ScheduleVote getVote(String voteId) {
        return getVoteOrThrow(voteId);
    }
}
