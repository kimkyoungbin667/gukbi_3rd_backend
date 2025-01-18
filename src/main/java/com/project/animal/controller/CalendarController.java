package com.project.animal.controller;

import com.project.animal.dto.calendar.EventDto;
import com.project.animal.mapper.UserMapper;
import com.project.animal.model.User;
import com.project.animal.service.CalendarService;
import com.project.animal.service.KakaoApiService;
import com.project.animal.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
@CrossOrigin(origins = "http://58.74.46.219:33333")
public class CalendarController {

    private final CalendarService calendarService;
    private final JwtUtil jwtUtil;
    private final KakaoApiService kakaoApiService;
    private final UserMapper userMapper;

    public CalendarController(CalendarService calendarService, JwtUtil jwtUtil, KakaoApiService kakaoApiService, UserMapper userMapper) {
        this.calendarService = calendarService;
        this.jwtUtil = jwtUtil;
        this.kakaoApiService = kakaoApiService;
        this.userMapper = userMapper;
    }

    // 일정 목록 가져오기
    @GetMapping("/events")
    public ResponseEntity<List<EventDto>> getAllEvents() {
        List<EventDto> events = calendarService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    // 특정 일정 가져오기
    @GetMapping("/events/{eventId}")
    public ResponseEntity<EventDto> getEventById(@PathVariable Long eventId) {
        EventDto event = calendarService.getEventById(eventId);
        return ResponseEntity.ok(event);
    }

    // 일정 추가
    @PostMapping("/events")
    public ResponseEntity<?> createEvent(
            @RequestHeader("Authorization") String token,
            @RequestBody EventDto eventDto) {
        if (eventDto.getTitle() == null || eventDto.getTitle().isEmpty()) {
            return ResponseEntity.badRequest().body("이벤트 제목이 필요합니다.");
        }
        if (eventDto.getEventDate() == null) {
            return ResponseEntity.badRequest().body("이벤트 날짜가 필요합니다.");
        }
        try {
            String actualToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getIdFromToken(actualToken);
            System.out.println("Extracted User ID: " + userId);

            User user = userMapper.findUserById(userId);
            if (user == null || user.getKakaoAccessToken() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("사용자 정보가 없습니다. 카카오 로그인이 필요합니다.");
            }

            String kakaoAccessToken = user.getKakaoAccessToken();

            // 스코프 확인 및 처리
            if (!kakaoApiService.checkTalkMessageScope(kakaoAccessToken)) {
                // 새로운 동의 요청 URL 생성
                String consentUrl = kakaoApiService.generateConsentRequestUrl();
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "추가 동의가 필요합니다.", "url", consentUrl));
            }

            // 이벤트 생성
            calendarService.createEvent(userId, eventDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("일정이 추가되었습니다.");
        } catch (Exception e) {
            System.err.println("JWT 토큰 처리 중 오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("일정 추가 실패: " + e.getMessage());
        }
    }

    @PostMapping("/oauth/callback")
    public ResponseEntity<?> handleOAuthCallback(@RequestBody Map<String, String> body) {
        try {
            // 요청 본문에서 code 추출
            String authorizationCode = body.get("code");
            System.out.println("수신한 카카오 인증 코드: " + authorizationCode);

            if (authorizationCode == null) {
                System.err.println("code 파라미터가 요청 본문에서 누락되었습니다.");
                return ResponseEntity.badRequest().body("code 파라미터가 누락되었습니다.");
            }

            // 카카오 API 호출 로그
            System.out.println("카카오 API 호출을 시작합니다.");
            String newAccessToken = kakaoApiService.getNewAccessToken(authorizationCode);
            System.out.println("카카오 API로부터 받은 새로운 액세스 토큰: " + newAccessToken);

            Long kakaoUserId = kakaoApiService.getKakaoUserId(newAccessToken);
            System.out.println("카카오 API로부터 받은 사용자 ID: " + kakaoUserId);

            // 사용자 DB 업데이트 로그
            User user = userMapper.findByKakaoId(String.valueOf(kakaoUserId));
            if (user == null) {
                System.err.println("카카오 사용자 ID에 해당하는 사용자를 찾을 수 없습니다.");
                throw new RuntimeException("해당 카카오 사용자를 찾을 수 없습니다.");
            }

            user.setKakaoAccessToken(newAccessToken);
            userMapper.updateUser(user);
            System.out.println("DB에 사용자 액세스 토큰 업데이트 완료.");

            // 스코프 확인 로그
            boolean isTalkMessageScopeAgreed = kakaoApiService.checkTalkMessageScope(newAccessToken);
            System.out.println("talk_message 스코프 상태 확인: " + isTalkMessageScopeAgreed);

            if (!isTalkMessageScopeAgreed) {
                System.err.println("talk_message 스코프 동의가 완료되지 않았습니다.");
                throw new RuntimeException("talk_message 스코프 동의가 완료되지 않았습니다.");
            }

            return ResponseEntity.ok("동의가 성공적으로 완료되었습니다.");
        } catch (Exception e) {
            System.err.println("OAuth 콜백 처리 중 오류 발생: " + e.getMessage());
            e.printStackTrace(); // 전체 스택 트레이스 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("OAuth 처리 실패: " + e.getMessage());
        }
    }



    // 일정 수정
    @PutMapping("/events/{eventId}")
    public ResponseEntity<String> updateEvent(@PathVariable Long eventId, @RequestBody EventDto eventDto) {
        calendarService.updateEvent(eventId, eventDto);
        return ResponseEntity.ok("일정이 수정되었습니다.");
    }

    // 일정 삭제
    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long eventId) {
        calendarService.deleteEvent(eventId);
        return ResponseEntity.ok("일정이 삭제되었습니다.");
    }
}

