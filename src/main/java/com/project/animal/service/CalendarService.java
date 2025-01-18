package com.project.animal.service;

import com.project.animal.dto.calendar.EventDto;
import com.project.animal.dto.notification.NotificationDto;
import com.project.animal.mapper.CalendarMapper;
import com.project.animal.mapper.NotificationMapper;
import com.project.animal.mapper.UserMapper;
import com.project.animal.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CalendarService {

    private final CalendarMapper calendarMapper;
    private final NotificationMapper notificationMapper;
    private final UserMapper userMapper; // 사용자 정보 조회를 위한 매퍼
    private final KakaoApiService kakaoApiService; // 카카오톡 메시지 전송 서비스

    public CalendarService(CalendarMapper calendarMapper, NotificationMapper notificationMapper, UserMapper userMapper, KakaoApiService kakaoApiService) {
        this.calendarMapper = calendarMapper;
        this.notificationMapper = notificationMapper;
        this.userMapper = userMapper;
        this.kakaoApiService = kakaoApiService;
    }

    // 모든 일정 가져오기
    public List<EventDto> getAllEvents() {
        return calendarMapper.findAllEvents();
    }

    // 특정 일정 가져오기
    public EventDto getEventById(Long eventId) {
        return calendarMapper.findEventById(eventId)
                .orElseThrow(() -> new RuntimeException("해당 일정이 존재하지 않습니다."));
    }

    // 일정 추가
    // CalendarService.java
    public void createEvent(Long userId, EventDto eventDto) {
        if (eventDto.getEventDate() == null || eventDto.getEventTime() == null) {
            throw new IllegalArgumentException("event_date와 event_time은 필수 입력값입니다.");
        }

        // 이벤트 저장
        calendarMapper.insertEvent(eventDto);

        // 생성된 이벤트 ID 가져오기
        Long generatedEventId = eventDto.getEventId();

        // 알림 생성
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setEventId(generatedEventId);
        notificationDto.setNotifyTime(eventDto.getEventDate().atTime(eventDto.getEventTime()));
        notificationDto.setIsSent(false);
        notificationMapper.insertNotification(notificationDto);

        // 사용자 정보 가져오기
        User user = userMapper.findUserById(userId);
        if (user == null || user.getKakaoAccessToken() == null) {
            System.err.println("사용자가 없거나 유효한 카카오 액세스 토큰이 없습니다.");
            return;
        }

        // 스코프 확인 및 동의 요청 처리
        if (!kakaoApiService.checkTalkMessageScope(user.getKakaoAccessToken())) {
            System.out.println("talk_message 스코프가 동의되지 않았습니다.");
            String consentUrl = kakaoApiService.generateConsentRequestUrl();
            throw new RuntimeException("추가 동의가 필요합니다. URL: " + consentUrl);
        }

        try {
            // 메시지 내용
            String message = String.format(
                    "새로운 일정이 등록되었습니다:\n제목: %s\n날짜: %s\n시간: %s",
                    eventDto.getTitle(),
                    eventDto.getEventDate(),
                    eventDto.getEventTime()
            );

            // 메시지 전송
            kakaoApiService.sendMessageToUser(user.getKakaoAccessToken(), message);
            System.out.println("카카오톡 메시지 전송 성공");
        } catch (Exception e) {
            System.err.println("카카오톡 메시지 전송 실패: " + e.getMessage());
        }
    }

    // 일정 수정
    public void updateEvent(Long eventId, EventDto eventDto) {
        EventDto existingEvent = getEventById(eventId); // 존재 여부 확인
        eventDto.setEventId(existingEvent.getEventId());
        calendarMapper.updateEvent(eventDto);
    }

    // 일정 삭제
    public void deleteEvent(Long eventId) {
        calendarMapper.deleteEvent(eventId);
    }

    @Transactional
    public Long createEventWithNotification(Long userId, EventDto eventDto) {
        // 이벤트 데이터 저장
        eventDto.setUserId(userId);
        calendarMapper.insertEvent(eventDto);

        // 생성된 이벤트 ID 가져오기
        Long eventId = eventDto.getEventId();

        // 알림 데이터 추가
        createNotification(eventId, eventDto);

        return eventId;
    }

    private void createNotification(Long eventId, EventDto eventDto) {
        LocalDateTime eventDateTime = LocalDateTime.of(eventDto.getEventDate(), eventDto.getEventTime());

        // 24시간 전 알림
        NotificationDto notification24Hours = new NotificationDto();
        notification24Hours.setEventId(eventId);
        notification24Hours.setNotifyTime(eventDateTime.minusDays(1)); // 24시간 전
        notification24Hours.setIsSent(false);
        notificationMapper.insertNotification(notification24Hours);

        // 1시간 전 알림
        NotificationDto notification1Hour = new NotificationDto();
        notification1Hour.setEventId(eventId);
        notification1Hour.setNotifyTime(eventDateTime.minusHours(1)); // 1시간 전
        notification1Hour.setIsSent(false);
        notificationMapper.insertNotification(notification1Hour);

        System.out.println("알림 데이터가 생성되었습니다: 24시간 전 및 1시간 전");
    }

}
