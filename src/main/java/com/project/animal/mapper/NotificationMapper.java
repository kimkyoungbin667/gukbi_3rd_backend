package com.project.animal.mapper;

import com.project.animal.dto.notification.NotificationDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface NotificationMapper {

    // 알림 생성
    void insertNotification(NotificationDto notificationDto);

    List<NotificationDto> findPendingNotifications(@Param("now") LocalDateTime now);
}
