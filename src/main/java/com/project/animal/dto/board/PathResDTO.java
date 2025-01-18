package com.project.animal.dto.board;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

// 산책 기록 반환 DTO
@Data
public class PathResDTO {
    
    private Long logId;                 // 산책 기록 아이디
    private String walkName;            // 산책 기록 이름
    private Date walkDate;              // 산책 기록한 일
    private LocalDateTime startTime;     // 기록 시작 시간
    private LocalDateTime endTime;      // 기록 종료 시간

}
