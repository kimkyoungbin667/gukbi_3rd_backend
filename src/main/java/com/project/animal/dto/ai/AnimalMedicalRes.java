package com.project.animal.dto.ai;

import lombok.Data;

// 의료기록 반환 DTO
@Data
public class AnimalMedicalRes {

    private String recordType;      // 기록 타입
    private String description;     // 설명
    private String clinicName;      // 병원이름
    private String vetName;         // 반려인 이름
    private String notes;           // 추가 내용
    private String createdAt;       // 진료일



}
