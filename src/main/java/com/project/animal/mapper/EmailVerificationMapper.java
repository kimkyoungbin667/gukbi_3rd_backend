package com.project.animal.mapper;

import com.project.animal.dto.user.EmailVerificationDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmailVerificationMapper {
    void insertVerificationCode(EmailVerificationDTO verification); // 인증 코드 저장
    EmailVerificationDTO getVerificationCodeByEmail(String email);  // 이메일로 인증 코드 가져오기
    void deleteExpiredCodes();                                   // 만료된 코드 삭제
    boolean isEmailExists(String email); // 이메일 존재 여부 확인
}
