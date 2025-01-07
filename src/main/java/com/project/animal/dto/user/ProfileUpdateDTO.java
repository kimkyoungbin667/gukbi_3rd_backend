package com.project.animal.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUpdateDTO {
    private String nickname; // 필드 이름은 프론트엔드 데이터와 일치해야 함
    private String profileUrl;
}
