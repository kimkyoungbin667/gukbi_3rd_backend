package com.project.animal.mapper;

import com.project.animal.dto.user.LoginDTO;
import com.project.animal.model.User;
import org.apache.ibatis.annotations.Mapper;
import com.project.animal.dto.user.RegisterDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    void registerUser(RegisterDTO registerDTO);
    User findByEmail(String userEmail);
    User findUserById(Long userId);
    void updateUserProfile(User user);
    // 리프레시 토큰 저장
    void saveRefreshToken(@Param("userId") Long userId,
                          @Param("refreshToken") String refreshToken);

    // 리프레시 토큰 조회
    String findRefreshTokenByUserId(@Param("userId") Long userId);

    // 리프레시 토큰 삭제
    void deleteRefreshToken(@Param("userId") Long userId);

    void updateUserPassword(User user);

    void deactivateUser(@Param("userId") Long userId); // 회원 탈퇴

    // 카카오 ID로 사용자 조회
    User findByKakaoId(@Param("kakaoId") String kakaoId);

    void updateUser(User user);
}
