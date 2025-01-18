package com.project.animal.dto.notification;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class NotificationEventDto {
    private Long notificationId; // 알림 ID
    private Long eventId; // 이벤트 ID
    private String title; // 이벤트 제목
    private LocalDate eventDate; // 이벤트 날짜
    private LocalTime eventTime; // 이벤트 시간
    private String userEmail; // 사용자 이메일
    private String socialType; // 소셜 타입 (KAKAO 또는 GENERAL)
    private String kakaoAccessToken; // 카카오 액세스 토큰
}
