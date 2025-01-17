package com.project.animal.service;

import com.project.animal.dto.notification.NotificationDto;
import com.project.animal.dto.notification.NotificationEventDto;
import com.project.animal.mapper.NotificationMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationScheduler {

    private final NotificationMapper notificationMapper;
    private final EmailService emailService; // 이메일 전송
    private final KakaoApiService kakaoApiService; // 카카오톡 메시지 전송

    public NotificationScheduler(NotificationMapper notificationMapper, EmailService emailService, KakaoApiService kakaoApiService) {
        this.notificationMapper = notificationMapper;
        this.emailService = emailService;
        this.kakaoApiService = kakaoApiService;
    }

    // 1분마다 실행
    @Scheduled(fixedRate = 60000)
    public void sendNotifications() {
        System.out.println("스케줄러 실행 중: " + LocalDateTime.now());
        LocalDateTime now = LocalDateTime.now();

        // 현재 시간에 도달한 알림 조회
        List<NotificationDto> pendingNotifications = notificationMapper.findPendingNotifications(now);

        System.out.println("발견된 알림 수: " + pendingNotifications.size());

        for (NotificationDto notification : pendingNotifications) {
            try {
                // 알림에 연결된 이벤트 및 사용자 정보 가져오기
                NotificationEventDto event = notificationMapper.findEventByNotification(notification.getNotificationId());

                if ("KAKAO".equals(event.getSocialType())) {
                    // 카카오톡 알림 템플릿
                    String message = String.format(
                            "🔔 다가오는 일정 알림\n\n제목: %s\n날짜: %s\n시간: %s\n\n웹에서 확인하세요!",
                            event.getTitle(),
                            event.getEventDate(),
                            event.getEventTime()
                    );
                    kakaoApiService.sendMessageToUser(event.getKakaoAccessToken(), message);
                    System.out.println("카카오톡 메시지 전송 성공");
                } else {
                    // 이메일 알림 템플릿
                    emailService.sendReminderNotification(
                            event.getUserEmail(),
                            "다가오는 일정 알림",
                            event.getTitle(),
                            event.getEventDate().toString(),
                            event.getEventTime().toString()
                    );
                    System.out.println("이메일 알림 전송 성공");
                }

                // 알림 상태 업데이트 (발송 완료)
                notificationMapper.updateNotificationStatus(notification.getNotificationId());
            } catch (Exception e) {
                System.err.println("알림 발송 실패: " + e.getMessage());
            }
        }
    }
}
