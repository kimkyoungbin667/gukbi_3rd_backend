package com.project.animal.dto.ai;

import lombok.Data;

@Data
public class AnimalPathRes {

    private Long logId;             // 기록 아이디
    private String walkDate;        // 산책 날
    private String distance;        // 걸린 거리
    private String duration;        // 총 걸린 시간
            
}
