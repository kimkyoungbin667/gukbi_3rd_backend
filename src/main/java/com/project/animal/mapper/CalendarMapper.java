package com.project.animal.mapper;

import com.project.animal.dto.calendar.EventDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CalendarMapper {

    // 모든 일정 조회
    List<EventDto> findAllEvents();

    // 특정 일정 조회
    Optional<EventDto> findEventById(Long eventId);

    // 일정 추가
    void insertEvent(EventDto eventDto);

    // 일정 수정
    void updateEvent(EventDto eventDto);

    // 일정 삭제
    void deleteEvent(Long eventId);
}
