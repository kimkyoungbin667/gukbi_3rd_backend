package com.project.animal.service;

import com.project.animal.dto.user.LoginDTO;
import com.project.animal.model.User;
import com.project.animal.util.JwtUtil;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.project.animal.dto.user.RegisterDTO;
import com.project.animal.mapper.UserMapper;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final KakaoApiService kakaoApiService;

    public UserService(UserMapper userMapper, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, KakaoApiService kakaoApiService) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.kakaoApiService = kakaoApiService;
    }

    // 회원 가입
    public void registerUser(RegisterDTO registerDTO) {
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(registerDTO.getUserPassword());
        System.out.println("Encoded password during registration: " + encodedPassword);
        registerDTO.setUserPassword(encodedPassword);
        userMapper.registerUser(registerDTO);
    }


    // 로그인
    public Map<String, String> login(LoginDTO loginDTO) {
        User user = userMapper.findByEmail(loginDTO.getEmail());
        if (user == null || !passwordEncoder.matches(loginDTO.getPassword(), user.getUserPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // 액세스 토큰 생성
        String accessToken = jwtUtil.generateToken(user.getUserIdx(), user.getUserEmail());

        // 리프레시 토큰 생성
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserIdx());

        // 리프레시 토큰 저장
        userMapper.saveRefreshToken(user.getUserIdx(), refreshToken);

        // 클라이언트로 반환할 토큰 맵 생성
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;
    }

    // 리프레시 토큰 검증 및 액세스 토큰 재발행
    public String refreshAccessToken(String refreshToken) {
        Long userId = jwtUtil.getIdFromToken(refreshToken);
        String storedRefreshToken = userMapper.findRefreshTokenByUserId(userId);

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        return jwtUtil.generateToken(userId, jwtUtil.getEmailFromToken(refreshToken));
    }

    // 카카오 로그인
    public Map<String, String> kakaoLogin(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String kakaoUserInfoUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>("", headers);

        ResponseEntity<Map> response = restTemplate.exchange(kakaoUserInfoUrl, HttpMethod.GET, entity, Map.class);

        Map<String, Object> kakaoAccount = (Map<String, Object>) response.getBody().get("kakao_account");
        String email = (String) kakaoAccount.get("email");
        String kakaoId = response.getBody().get("id").toString();

        // 카카오 ID로 사용자 조회
        User user = userMapper.findByKakaoId(kakaoId);
        if (user == null) {
            // 새 사용자 등록을 위한 DTO 생성
            RegisterDTO registerDTO = new RegisterDTO();
            registerDTO.setUserEmail(email);
            registerDTO.setUserName((String) ((Map<String, Object>) response.getBody().get("properties")).get("nickname"));
            registerDTO.setKakaoId(kakaoId);
            registerDTO.setSocialType("KAKAO");

            // 새 사용자 등록
            userMapper.registerUser(registerDTO);

            // 등록 후 사용자 정보 다시 조회
            user = userMapper.findByKakaoId(kakaoId);
        }

        // 토큰 정보 저장
        user.setKakaoAccessToken(accessToken);
        user.setKakaoTokenExpiry(LocalDateTime.now().plusHours(2)); // 카카오 토큰 만료 시간 설정
        userMapper.updateUserProfile(user);

        // JWT 생성
        String newAccessToken = jwtUtil.generateToken(user.getUserIdx(), user.getUserEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserIdx());

        userMapper.saveRefreshToken(user.getUserIdx(), refreshToken);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;
    }


    public void logout(Long userId) {
        // 리프레시 토큰 삭제
        userMapper.deleteRefreshToken(userId);
    }

    // 사용자 정보
    public User findUserProfileById(Long userId) {
        return userMapper.findUserById(userId);
    }

    // 처음 프로필 업데이트
    public void updateUserProfile(Long userId, String userNickname, String userProfileUrl) {
        User user = userMapper.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        user.setUserNickname(userNickname);
        user.setUserProfileUrl(userProfileUrl);

        userMapper.updateUserProfile(user);
    }

    // 닉네임 찾기
    public String findNicknameById(Long userId) {
        User user = userMapper.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
        return user.getUserNickname();
    }

    // 비밀번호 변경
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userMapper.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, user.getUserPassword())) {
            throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 암호화
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.setUserPassword(encodedNewPassword);

        // 비밀번호 업데이트
        userMapper.updateUserPassword(user);
    }

    // 회원 탈퇴
    public void deactivateUser(Long userId) {
        User user = userMapper.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        userMapper.deactivateUser(userId);
    }

    public void sendMessage(Long userId, String message) {
        User user = userMapper.findUserById(userId);

        if (user == null || user.getKakaoAccessToken() == null) {
            throw new RuntimeException("사용자가 없거나 카카오 액세스 토큰이 없습니다.");
        }

        // 카카오 액세스 토큰 만료 여부 확인
        if (user.getKakaoTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("카카오 액세스 토큰이 만료되었습니다. 다시 로그인하세요.");
        }

        // 메시지 전송
        kakaoApiService.sendMessageToUser(user.getKakaoAccessToken(), message);
    }

    // UserService.java
    public void updateKakaoAccessToken(String newAccessToken) {
        Long userId = jwtUtil.getIdFromToken(newAccessToken);
        User user = userMapper.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        user.setKakaoAccessToken(newAccessToken);
        user.setKakaoTokenExpiry(LocalDateTime.now().plusHours(2)); // 만료 시간 갱신
        userMapper.updateUserProfile(user);
    }

}
