package com.project.animal.dto.pet;

import lombok.Data;

@Data
public class PetDetailsRequestDto {
    private Long petId; // 애완동물 ID
    private String birthDate; // 생년월일
    private String healthStatus; // 건강 상태
    private String dietaryRequirements; // 식단 요구사항
    private String allergies; // 알레르기 정보
    private String notes; // 특이 사항
}
