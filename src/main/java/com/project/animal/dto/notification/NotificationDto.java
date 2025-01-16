package com.project.animal.dto.notification;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationDto {
    private Long notificationId;
    private Long eventId;
    private LocalDateTime notifyTime;
    private Boolean isSent; // 기본값 false로 설정
}
