package com.project.animal.dto.calendar;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class EventDto {
    private Long eventId;
    private Long petId;           // 프론트엔드의 "petId"와 일치해야 함
    private String title;         // "title"과 일치
    private String description;   // "description"과 일치
    private LocalDate eventDate;  // "eventDate"와 일치
    private LocalTime eventTime;  // "eventTime"과 일치
    private Long userId;          // "userId"와 일치
}

