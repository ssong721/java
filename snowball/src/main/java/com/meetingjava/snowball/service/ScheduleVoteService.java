package com.meetingjava.snowball.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetingjava.snowball.entity.ScheduleVote;
import com.meetingjava.snowball.repository.ScheduleVoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ScheduleVoteService {

    private final ScheduleVoteRepository voteRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public ScheduleVoteService(ScheduleVoteRepository voteRepository,
                               RestTemplate restTemplate,
                               ObjectMapper objectMapper) {
        this.voteRepository = voteRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    // ✅ 투표 생성
    public ScheduleVote createVote(Date start, Date end, int durationMinutes, Long meetingId) {
        ScheduleVote vote = new ScheduleVote(start, end, durationMinutes, meetingId);
        return voteRepository.save(vote); // DB에 저장
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

    // ✅ Gemini API 연동
    public void recommendUsingGemini(String voteId) {
        ScheduleVote vote = getVoteOrThrow(voteId);

        try {
            String geminiApiUrl = "https://your-gemini-api.com/recommend";
            String jsonBody = vote.prepareVoteDataForGPT();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer YOUR_GEMINI_API_KEY");

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

    // ✅ voteId로 단일 조회 (JSON 출력용)
    public ScheduleVote findById(String voteId) {
        return voteRepository.findById(voteId)
                .orElseThrow(() -> new NoSuchElementException("해당 voteId 없음: " + voteId));
    }

    // ✅ 전체 조회 (voteId 목록 확인용)
    public List<ScheduleVote> findAll() {
        return voteRepository.findAll();
    }

    // ✅ 내부 공통 조회 메서드
    private ScheduleVote getVoteOrThrow(String voteId) {
        return voteRepository.findById(voteId)
                .orElseThrow(() -> new NoSuchElementException("해당 voteId 없음: " + voteId));
    }

    // ✅ 단일 투표 조회 메서드 (직관적 이름)
    public ScheduleVote getVote(String voteId) {
        return voteRepository.findById(voteId)
                .orElseThrow(() -> new NoSuchElementException("해당 voteId 없음: " + voteId));
    }

}
