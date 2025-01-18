package com.project.animal.controller;

import com.project.animal.dto.calendar.EventDto;
import com.project.animal.mapper.UserMapper;
import com.project.animal.model.User;
import com.project.animal.service.CalendarService;
import com.project.animal.service.EmailService;
import com.project.animal.service.KakaoApiService;
import com.project.animal.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
@CrossOrigin(origins = "http://localhost:3000")
public class CalendarController {

    private final CalendarService calendarService;
    private final JwtUtil jwtUtil;
    private final KakaoApiService kakaoApiService;
    private final UserMapper userMapper;
    private final EmailService emailService;

    public CalendarController(CalendarService calendarService, JwtUtil jwtUtil, KakaoApiService kakaoApiService, UserMapper userMapper, EmailService emailService) {
        this.calendarService = calendarService;
        this.jwtUtil = jwtUtil;
        this.kakaoApiService = kakaoApiService;
        this.userMapper = userMapper;
        this.emailService = emailService;
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
        try {
            // 입력값 검증
            if (eventDto.getTitle() == null || eventDto.getTitle().isEmpty()) {
                return ResponseEntity.badRequest().body("이벤트 제목이 필요합니다.");
            }
            if (eventDto.getEventDate() == null) {
                return ResponseEntity.badRequest().body("이벤트 날짜가 필요합니다.");
            }

            String actualToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getIdFromToken(actualToken);
            System.out.println("Extracted User ID: " + userId);

            User user = userMapper.findUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사용자 정보를 찾을 수 없습니다.");
            }

            // 소셜 타입에 따른 처리
            if ("KAKAO".equals(user.getSocialType())) {
                System.out.println("카카오 사용자 처리 시작");

                if (!kakaoApiService.checkTalkMessageScope(user.getKakaoAccessToken())) {
                    String consentUrl = kakaoApiService.generateConsentRequestUrl();
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of("message", "추가 동의가 필요합니다.", "url", consentUrl));
                }

                // 카카오톡 메시지 전송
                try {
                    String message = String.format(
                            "새로운 일정이 등록되었습니다:\n제목: %s\n날짜: %s\n시간: %s",
                            eventDto.getTitle(),
                            eventDto.getEventDate(),
                            eventDto.getEventTime()
                    );
                    kakaoApiService.sendMessageToUser(user.getKakaoAccessToken(), message);
                    System.out.println("카카오톡 메시지 전송 성공");
                } catch (Exception e) {
                    System.err.println("카카오톡 메시지 전송 실패: " + e.getMessage());
                }
            } else {
                // 이메일 알림 전송
                emailService.sendEventCreationNotification(
                        user.getUserEmail(),
                        eventDto.getTitle(),
                        eventDto.getEventDate().toString(),
                        String.valueOf(eventDto.getEventTime())
                );
                System.out.println("일반 사용자 이메일 처리 완료");
            }

            // 이벤트 생성 및 알림 데이터 추가
            Long eventId = calendarService.createEventWithNotification(userId, eventDto);

            return ResponseEntity.status(HttpStatus.CREATED).body("일정이 추가되었습니다.");
        } catch (Exception e) {
            System.err.println("일정 추가 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("일정 추가 실패: " + e.getMessage());
        }
    }


    @PostMapping("/oauth/callback")
    public ResponseEntity<?> handleOAuthCallback(@RequestBody Map<String, String> body) {
        try {
            String authorizationCode = body.get("code");
            System.out.println("수신한 카카오 인증 코드: " + authorizationCode);

            if (authorizationCode == null) {
                return ResponseEntity.badRequest().body("code 파라미터가 누락되었습니다.");
            }

            String newAccessToken = kakaoApiService.getNewAccessToken(authorizationCode);
            System.out.println("카카오 API로부터 받은 새로운 액세스 토큰: " + newAccessToken);

            Long kakaoUserId = kakaoApiService.getKakaoUserId(newAccessToken);
            System.out.println("카카오 API로부터 받은 사용자 ID: " + kakaoUserId);

            User user = userMapper.findByKakaoId(String.valueOf(kakaoUserId));
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 카카오 사용자를 찾을 수 없습니다.");
            }

            user.setKakaoAccessToken(newAccessToken);
            userMapper.updateUser(user);
            System.out.println("DB에 사용자 액세스 토큰 업데이트 완료.");

            boolean isTalkMessageScopeAgreed = kakaoApiService.checkTalkMessageScope(newAccessToken);
            if (!isTalkMessageScopeAgreed) {
                return ResponseEntity.badRequest().body("talk_message 스코프 동의가 완료되지 않았습니다.");
            }

            return ResponseEntity.ok("동의가 성공적으로 완료되었습니다.");
        } catch (Exception e) {
            System.err.println("OAuth 콜백 처리 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("OAuth 처리 실패: " + e.getMessage());
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
