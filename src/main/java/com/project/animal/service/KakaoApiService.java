package com.project.animal.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class KakaoApiService {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String CLIENT_ID = "3827b8cf4edc30ac5382f213f8279463"; // 카카오 REST API 키
    private static final String REDIRECT_URI = "http://localhost:3000/eventcaledar";

    /**
     * 사용자 스코프 확인
     * @param accessToken 사용자의 액세스 토큰
     * @return talk_message 스코프 동의 여부
     */
    public boolean checkTalkMessageScope(String accessToken) {
        String url = "https://kapi.kakao.com/v2/user/scopes";
        HttpHeaders headers = createHeaders(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            System.out.println("스코프 확인 요청 URL: " + url);
            System.out.println("스코프 확인 요청 헤더: " + headers);
            System.out.println("스코프 응답 데이터: " + response.getBody());

            List<Map<String, Object>> scopes = (List<Map<String, Object>>) response.getBody().get("scopes");
            for (Map<String, Object> scope : scopes) {
                if ("talk_message".equals(scope.get("id"))) {
                    Boolean isAgreed = (Boolean) scope.get("agreed");
                    System.out.println("talk_message 스코프 상태: " + isAgreed);
                    return Boolean.TRUE.equals(isAgreed);
                }
            }
        } catch (Exception e) {
            System.err.println("스코프 확인 중 오류 발생: " + e.getMessage());
        }

        return false;
    }

    /**
     * 카카오 사용자 ID 가져오기
     * @param accessToken 사용자의 액세스 토큰
     * @return 카카오 사용자 ID
     */
    public Long getKakaoUserId(String accessToken) {
        String url = "https://kapi.kakao.com/v1/user/access_token_info";

        HttpHeaders headers = createHeaders(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            Long userId = ((Number) response.getBody().get("id")).longValue(); // 카카오 사용자 ID 추출
            System.out.println("카카오 사용자 ID: " + userId);
            return userId;
        } catch (Exception e) {
            System.err.println("카카오 사용자 ID 가져오기 실패: " + e.getMessage());
            throw new RuntimeException("카카오 사용자 ID 가져오기 실패: " + e.getMessage());
        }
    }


    /**
     * 추가 동의 요청 URL 생성
     * @return 동의 요청 URL
     */
    public String generateConsentRequestUrl() {
        String url = String.format(
                "https://kauth.kakao.com/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code&scope=talk_message",
                CLIENT_ID, REDIRECT_URI
        );
        System.out.println("동의 요청 URL: " + url);
        return url;
    }

    /**
     * 새로운 액세스 토큰 발급
     * @param authorizationCode 카카오 인증 코드
     * @return 새로운 액세스 토큰
     */
    public String getNewAccessToken(String authorizationCode) {
        String url = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", CLIENT_ID);
        params.add("redirect_uri", REDIRECT_URI);
        params.add("code", authorizationCode);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            String newAccessToken = (String) response.getBody().get("access_token");
            System.out.println("새로운 액세스 토큰 발급 성공: " + newAccessToken);
            return newAccessToken;
        } catch (Exception e) {
            System.err.println("새로운 액세스 토큰 발급 중 오류 발생: " + e.getMessage());
            throw new RuntimeException("토큰 발급 실패: " + e.getMessage());
        }
    }

    /**
     * 카카오톡 메시지 전송
     * @param accessToken 사용자의 액세스 토큰
     * @param message 전송할 메시지
     */
    // 카카오톡 메시지 전송 메서드
    public void sendMessageToUser(String accessToken, String message) {
        String url = "https://kapi.kakao.com/v2/api/talk/memo/default/send";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 템플릿 데이터 생성
        String templateObject = String.format(
                "{\"object_type\":\"text\",\"text\":\"%s\",\"link\":{\"web_url\":\"http://example.com\",\"mobile_web_url\":\"http://example.com\"},\"button_title\":\"확인\"}",
                message.replace("\n", "\\n") // 줄바꿈 처리
        );

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("template_object", templateObject);

        // 템플릿 데이터 로그 출력
        System.out.println("템플릿 데이터: " + templateObject);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            System.out.println("카카오톡 메시지 전송 성공: " + response.getBody());
        } catch (Exception e) {
            System.err.println("카카오톡 API 호출 중 오류 발생: " + e.getMessage());
            throw new RuntimeException("카카오 메시지 전송 실패: " + e.getMessage());
        }
    }

    /**
     * 액세스 토큰을 포함한 HTTP 헤더 생성
     * @param accessToken 사용자의 액세스 토큰
     * @return HTTP 헤더
     */
    private HttpHeaders createHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        return headers;
    }
}
