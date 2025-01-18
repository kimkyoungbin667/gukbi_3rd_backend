package com.project.animal.mapper;

import com.project.animal.dto.notification.NotificationEventDto;
import com.project.animal.dto.notification.NotificationDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface NotificationMapper {

    // 알림 생성
    void insertNotification(NotificationDto notificationDto);

    // 현재 시간에 도달한 알림 조회
    List<NotificationDto> findPendingNotifications(@Param("now") LocalDateTime now);

    // 알림 상태 업데이트
    void updateNotificationStatus(@Param("notificationId") Long notificationId);

    // 알림 ID를 기반으로 이벤트 및 사용자 정보 조회
    NotificationEventDto findEventByNotification(@Param("notificationId") Long notificationId);
}

