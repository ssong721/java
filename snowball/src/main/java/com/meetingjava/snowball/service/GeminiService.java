package com.meetingjava.snowball.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

@Service
public class GeminiService {

    @Value("${gemini.api.key}") // ✅ application.properties에서 키 불러오기
    private String apiKey;

    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent";
    private final RestTemplate restTemplate;

    @Autowired
    public GeminiService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public String getRecommendedTime(String voteData) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey); // ✅ 추가된 부분

        String prompt = "다음은 모임원들의 시간 투표 결과입니다. 가장 많은 인원이 가능한 추천 일정을 알려줘:\n" + voteData;

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    GEMINI_URL + "?key=" + apiKey,
                    entity,
                    Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> resBody = response.getBody();
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) resBody.get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                    List<Map<String, String>> parts = (List<Map<String, String>>) content.get("parts");
                    if (parts != null && !parts.isEmpty()) {
                        return parts.get(0).get("text");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Gemini 호출 오류: " + e.getMessage());
        }

        return "Gemini 추천 시간 없음 또는 오류 발생";
    }
}
