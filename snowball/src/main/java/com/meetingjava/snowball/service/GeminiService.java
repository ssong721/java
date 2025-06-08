package com.meetingjava.snowball.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String geminiUrl;

    private final RestTemplate restTemplate;

    public GeminiService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public String getRecommendedTime(String voteData) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String prompt = """
            다음은 모임원들의 시간 투표 결과입니다.
            각 사용자는 가능한 시간을 여러 개 선택했습니다.
            가장 많은 인원이 가능한 하나의 일정을 추천해주세요.
            ISO 8601 형식(예: 2025-06-07T18:00:00)으로 출력해줘.

            """ + voteData;

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    geminiUrl + "?key=" + apiKey,
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

        return null;
    }
}