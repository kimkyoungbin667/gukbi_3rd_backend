package com.project.animal.controller;

import com.project.animal.dto.user.LoginDTO;
import com.project.animal.dto.user.UserDTO;
import com.project.animal.model.User;
import com.project.animal.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.animal.dto.user.RegisterDTO;
import com.project.animal.service.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:3000") // 3000 포트의 클라이언트 허용
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterDTO registerDTO) {
        if (registerDTO.getUserEmail() == null || registerDTO.getUserEmail().isEmpty()) {
            return ResponseEntity.badRequest().body("이메일은 필수 항목입니다.");
        }
        userService.registerUser(registerDTO);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDTO loginDTO) {
        String token = userService.login(loginDTO);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/kakao-login")
    public ResponseEntity<Map<String, String>> kakaoLogin(@RequestBody Map<String, String> kakaoData) {
        String accessToken = kakaoData.get("access_token");

        try {
            // 카카오 API 호출 및 사용자 처리
            User user = userService.kakaoLogin(accessToken);

            // JWT 생성
            String token = jwtUtil.generateToken(user.getUserEmail());

            // 응답 데이터 생성
            Map<String, String> response = new HashMap<>();
            response.put("token", token);

            return ResponseEntity.ok(response); // JWT 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "카카오 로그인 실패: " + e.getMessage()));
        }
    }





    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getUserProfile(@RequestHeader("Authorization") String token) {
        // 토큰에서 이메일 추출
        String email = jwtUtil.getEmailFromToken(token.replace("Bearer ", ""));

        // 이메일로 사용자 정보 조회
        User user = userService.findUserProfileByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // User 객체를 UserDTO로 변환
        UserDTO userDTO = new UserDTO(
                user.getUserIdx(),
                user.getUserName(),
                user.getUserNickname(),
                user.getUserEmail(),
                user.getUserBirth(),
                null // 비밀번호는 제외
        );

        return ResponseEntity.ok(userDTO);
    }



}
