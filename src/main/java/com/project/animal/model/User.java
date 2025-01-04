package com.project.animal.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

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
}
