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
        registerDTO.setUserPassword(encodedPassword);
        userMapper.registerUser(registerDTO);
    }

    // 로그인
    public String login(LoginDTO loginDTO) {
        User user = userMapper.findByEmail(loginDTO.getEmail());
        if (user == null) {
            throw new RuntimeException("Invalid email or password");
        }
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getUserPassword())) {
            boolean isMatch = passwordEncoder.matches(loginDTO.getPassword(), user.getUserPassword());
        }

        return jwtUtil.generateToken(user.getUserEmail());
    }

    public User kakaoLogin(String accessToken) {
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

        return user;
    }



    // 사용자 정보
    public User findUserProfileByEmail(String email) {
        return userMapper.findUserProfileByEmail(email);
    }
}
