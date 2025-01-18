package com.project.animal.dto.ai;

import lombok.Data;

// 반려동물 상세정보 반환 DTO
@Data
public class AnimalDetailInfoRes {

    private String birthDate;           // 생일
    private String healthStatus;        // 건강 상태
    private String dietaryRequirements; // 식단 요구사항
    private String allergies;           // 알러지 사항
    private String notes;            // 메모
    private String updatedAt;       // 업데이트 날짜

}
