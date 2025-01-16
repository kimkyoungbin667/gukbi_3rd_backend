package com.project.animal.dto.ai;

import lombok.Data;

@Data
public class AnimalResDTO {

    private Long petId;         // 펫 인덱스
    private String dogName;     // 반려동물 이름 
    private String kindName;    // 종류
    private String profileUrl;  // 프로필 사진

}
