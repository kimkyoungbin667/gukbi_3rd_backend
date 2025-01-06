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

            // JWT 생성 (ID와 이메일 포함)
            String token = jwtUtil.generateToken(user.getUserIdx(), user.getUserEmail());

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
        try {
            String actualToken = token.replace("Bearer ", "");
            System.out.println("Received token: " + actualToken);

            if (!jwtUtil.validateToken(actualToken)) {
                System.out.println("Invalid token.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            Long userId = jwtUtil.getIdFromToken(actualToken);
            System.out.println("Extracted user ID: " + userId);

            User user = userService.findUserProfileById(userId);
            if (user == null) {
                System.out.println("No user found for ID: " + userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            UserDTO userDTO = new UserDTO(
                    user.getUserIdx(),
                    user.getUserName(),
                    user.getUserNickname(),
                    user.getUserEmail(),
                    user.getUserBirth(),
                    null,
                    user.getUserProfileUrl()
            );

            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            System.err.println("Error in getUserProfile: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }




}
