package com.project.animal.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserDTO {
    private Long userIdx; // 사용자 인덱스
    private String userName; // 이름
    private String userNickname; // 닉네임
    private String userEmail; // 이메일
    private LocalDate userBirth; // 생년월일
    private String userPassword; // 비밀번호
    private String userProfileUrl; // 프로필 URL
    private String socialType; // 소셜 타입 추가

    // 생성자
    public UserDTO(Long userIdx, String userName, String userNickname, String userEmail, LocalDate userBirth, String userPassword, String userProfileUrl, String socialType) {
        this.userIdx = userIdx;
        this.userName = userName;
        this.userNickname = userNickname;
        this.userEmail = userEmail;
        this.userBirth = userBirth;
        this.userPassword = userPassword;
        this.userProfileUrl = userProfileUrl;
        this.socialType = socialType; // 소셜 타입 초기화
    }
}

