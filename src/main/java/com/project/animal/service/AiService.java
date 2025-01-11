package com.project.animal.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AiService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    /**
     * GPT-3.5 Turbo와 통신하여 응답을 반환하는 메서드
     * @param prompt React에서 전달받은 질문 또는 요청
     * @return GPT의 응답
     */
    public String getChatResponse(String prompt) {
        RestTemplate restTemplate = new RestTemplate();

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);  // API Key 인증

        // 요청 본문 설정
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");  // GPT 모델
        requestBody.put("messages", new Object[]{
                Map.of("role", "user", "content", prompt)
        });
        requestBody.put("max_tokens", 500);  // 최대 토큰 수

        // HTTP 요청 생성
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            // OpenAI API 호출
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, Map.class);

            // 응답 파싱
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("choices")) {
                Map<String, Object> choice = (Map<String, Object>) ((java.util.List) responseBody.get("choices")).get(0);
                Map<String, Object> message = (Map<String, Object>) choice.get("message");
                return (String) message.get("content");
            } else {
                return "응답을 가져오지 못했습니다.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "오류가 발생했습니다.";
        }
    }
}
