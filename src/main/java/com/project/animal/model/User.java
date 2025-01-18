package com.project.animal.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class User {
    private Long userIdx;
    private String userName;
    private String userNickname;
    private String userEmail;
    private LocalDate userBirth;
    private String userPassword;
    private String userProfileUrl; // 프로필 URL 필드
    private String kakaoId; // Kakao ID 추가
    private String socialType;
    private String kakaoAccessToken; // 카카오 액세스 토큰
    private String kakaoRefreshToken; // 카카오 리프레시 토큰
    private LocalDateTime kakaoTokenExpiry;
    private Boolean isAdmin; // 관리자 여부
    private Boolean isActive; // 계정 활성화 여부
}
