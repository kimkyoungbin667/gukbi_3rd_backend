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

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
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
        // 비밀번호가 없는 경우 기본값 설정
        if (registerDTO.getUserPassword() == null || registerDTO.getUserPassword().isEmpty()) {
            String defaultPassword =  generateRandomPassword(10);
            String encodedPassword = passwordEncoder.encode(defaultPassword); // 암호화된 비밀번호
            registerDTO.setUserPassword(encodedPassword);
        } else {
            String encodedPassword = passwordEncoder.encode(registerDTO.getUserPassword());
            registerDTO.setUserPassword(encodedPassword);
        }

        userMapper.registerUser(registerDTO);
    }

    // 로그인
    public Map<String, String> login(LoginDTO loginDTO) {
        User user = userMapper.findByEmail(loginDTO.getEmail());

        if (user == null) {
            throw new RuntimeException("등록된 사용자가 없습니다.");
        }

        // 계정 활성화 여부 확인
        if (!user.getIsActive()) {
            throw new RuntimeException("비활성화된 계정입니다. 관리자에게 문의하세요.");
        }

        // 일반 로그인 처리
        if (user.getSocialType() == null || user.getSocialType().equalsIgnoreCase("GENERAL")) {
            if (!passwordEncoder.matches(loginDTO.getPassword(), user.getUserPassword())) {
                throw new RuntimeException("비밀번호가 올바르지 않습니다.");
            }
        } else {
            // 소셜 로그인 처리 (비밀번호 검증 없음)
            if (loginDTO.getPassword() != null && !loginDTO.getPassword().isEmpty()) {
                throw new RuntimeException("소셜 로그인은 비밀번호를 사용하지 않습니다.");
            }
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
        tokens.put("isAdmin", String.valueOf(user.getIsAdmin())); // 관리자 여부 반환
        tokens.put("isActive", String.valueOf(user.getIsActive())); // 활성화 여부 반환

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

        User user = userMapper.findByKakaoId(kakaoId);
        if (user == null) {
            // 새 사용자 등록
            RegisterDTO registerDTO = new RegisterDTO();
            registerDTO.setUserEmail(email != null ? email : "no-email");
            registerDTO.setUserName((String) ((Map<String, Object>) response.getBody().get("properties")).get("nickname"));
            registerDTO.setKakaoId(kakaoId);
            registerDTO.setSocialType("KAKAO");

            // 기본 비밀번호 설정
            String randomPassword = generateRandomPassword(10);
            registerDTO.setUserPassword(passwordEncoder.encode(randomPassword));

            userMapper.registerUser(registerDTO);
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
        tokens.put("isAdmin", String.valueOf(user.getIsAdmin())); // 관리자 여부 반환
        tokens.put("isActive", String.valueOf(user.getIsActive())); // 활성화 여부 반환

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

    public boolean isKakaoIdExists(String kakaoId) {
        return userMapper.isKakaoIdExists(kakaoId);
    }

    // 랜덤 문자열 생성 메서드
    private String generateRandomPassword(int length) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes).substring(0, length);
    }

    // 닉네임 중복 확인
    public boolean isNicknameAvailable(String nickname) {
        System.out.println("닉네임 중복 검사 요청: " + nickname);
        return userMapper.isNicknameAvailable(nickname);
    }

    // 특정 사용자가 작성한 게시글 가져오기
    public List<Map<String, Object>> getUserPosts(Long userId) {
        return userMapper.getUserPosts(userId);  // UserMapper에서 작성한 쿼리 호출
    }


}
