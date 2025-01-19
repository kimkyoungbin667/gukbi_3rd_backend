package com.project.animal.dto.map;

import lombok.Data;

import java.util.List;

@Data
public class WalksGetRes {
    private Long logId;         // 산책 로그 ID
    private Long petId;
    private String walkName;    // 산책 이름
    private String walkDate;    // 산책 날짜
    private String startTime;   // 시작 시간
    private String endTime;     // 종료 시간
    private String distance;    // 산책 거리
    private String duration;    // 산책 시간
    private List<PathsGetRes> paths;
}
