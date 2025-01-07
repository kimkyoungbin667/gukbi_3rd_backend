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

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserMapper userMapper, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
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

    //카카오 로그인
    public Map<String, String> kakaoLogin(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String kakaoUserInfoUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>("", headers);

        ResponseEntity<Map> response;
        try {
            response = restTemplate.exchange(kakaoUserInfoUrl, HttpMethod.GET, entity, Map.class);
            System.out.println("카카오 API 호출 성공: " + response.getBody());
        } catch (Exception e) {
            System.err.println("카카오 API 호출 실패: " + e.getMessage());
            throw new RuntimeException("카카오 API 호출 실패: " + e.getMessage(), e);
        }

        Map<String, Object> kakaoAccount = (Map<String, Object>) response.getBody().get("kakao_account");
        if (kakaoAccount == null || kakaoAccount.get("email") == null) {
            throw new RuntimeException("카카오 계정에서 이메일 정보를 가져올 수 없습니다.");
        }

        String email = (String) kakaoAccount.get("email");

        User user = userMapper.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setUserEmail(email);
            user.setUserName((String) ((Map<String, Object>) response.getBody().get("properties")).get("nickname"));
            user.setKakaoId(response.getBody().get("id").toString());
            user.setSocialType("KAKAO");

            RegisterDTO registerDTO = new RegisterDTO();
            registerDTO.setUserEmail(user.getUserEmail());
            registerDTO.setUserName(user.getUserName());
            registerDTO.setKakaoId(user.getKakaoId());
            registerDTO.setSocialType(user.getSocialType());

            userMapper.registerUser(registerDTO);
        }

        // 액세스 토큰 및 리프레시 토큰 생성
        String newAccessToken = jwtUtil.generateToken(user.getUserIdx(), user.getUserEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserIdx());

        // 리프레시 토큰 저장
        userMapper.saveRefreshToken(user.getUserIdx(), refreshToken);

        // 반환할 토큰 맵 생성
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;
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


}
