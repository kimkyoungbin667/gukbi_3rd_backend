package com.project.animal.dto.user;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDTO {
    private String userName;
    private String userEmail;
    private LocalDate userBirth;
    private String userPassword;
    private String kakaoId; // 카카오 ID 추가
    private String socialType; // 소셜 타입 추가
    private String userProfileUrl; // 프로필 URL 추가
}
