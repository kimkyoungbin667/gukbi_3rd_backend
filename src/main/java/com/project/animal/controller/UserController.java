package com.project.animal.controller;

import com.project.animal.dto.user.LoginDTO;
import com.project.animal.dto.user.ProfileUpdateDTO;
import com.project.animal.dto.user.UserDTO;
import com.project.animal.model.User;
import com.project.animal.service.FileService;
import com.project.animal.service.KakaoApiService;
import com.project.animal.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.animal.dto.user.RegisterDTO;
import com.project.animal.service.UserService;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://58.74.46.219:33333")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final FileService fileService;
    private final KakaoApiService kakaoApiService;

    public UserController(UserService userService, JwtUtil jwtUtil, FileService fileService, KakaoApiService kakaoApiService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.fileService = fileService;
        this.kakaoApiService = kakaoApiService;
    }

    //리프레쉬 토큰
    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshAccessToken(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("refreshToken");

            if (refreshToken == null || refreshToken.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Refresh token is missing"));
            }

            // 새 액세스 토큰 발행
            String newAccessToken = userService.refreshAccessToken(refreshToken);

            // 응답으로 새 액세스 토큰 반환
            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
        }
    }

    //회원가입
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterDTO registerDTO) {
        if (registerDTO.getUserEmail() == null || registerDTO.getUserEmail().isEmpty()) {
            return ResponseEntity.badRequest().body("이메일은 필수 항목입니다.");
        }
        userService.registerUser(registerDTO);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    //일반 로그인
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDTO loginDTO) {
        try {
            // 로그인 시 액세스 토큰과 리프레시 토큰을 함께 반환
            Map<String, String> tokens = userService.login(loginDTO);
            return ResponseEntity.ok(tokens);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
        }
    }

    // 카카오 로그인
    @PostMapping("/kakao-login")
    public ResponseEntity<Map<String, String>> kakaoLogin(@RequestBody Map<String, String> kakaoData) {
        String kakaoAccessToken = kakaoData.get("access_token");

        try {
            Map<String, String> tokens = userService.kakaoLogin(kakaoAccessToken);
            return ResponseEntity.ok(tokens);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "카카오 로그인 실패: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token == null) {
                System.out.println("Authorization 헤더가 없습니다.");
                return ResponseEntity.badRequest().body("Authorization 헤더가 없습니다.");
            }

            System.out.println("Authorization 헤더: " + token);

            String actualToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getIdFromToken(actualToken);
            System.out.println("추출된 UserID: " + userId);

            userService.logout(userId);
            return ResponseEntity.ok("로그아웃 완료");
        } catch (ExpiredJwtException e) {
            System.out.println("JWT 만료 예외 발생: " + e.getMessage());
            return ResponseEntity.ok("JWT 만료: 로그아웃 처리 완료");
        } catch (Exception e) {
            System.out.println("로그아웃 처리 중 예외 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "로그아웃 처리 실패", "message", e.getMessage()));
        }
    }

    //사용자 정보 불러오기
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getUserProfile(@RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.replace("Bearer ", "");
            if (!jwtUtil.validateToken(actualToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            Long userId = jwtUtil.getIdFromToken(actualToken);
            User user = userService.findUserProfileById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            UserDTO userDTO = new UserDTO(
                    user.getUserIdx(),
                    user.getUserName(),
                    user.getUserNickname(),
                    user.getUserEmail(),
                    user.getUserBirth(),
                    null,
                    user.getUserProfileUrl(),
                    user.getSocialType() // 소셜 타입 추가
            );


            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 닉네임 가져오기
    @GetMapping("/nickname")
    public ResponseEntity<String> getNicknameFromToken(@RequestHeader("Authorization") String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                throw new IllegalArgumentException("Authorization header is missing or empty");
            }
            String actualToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getIdFromToken(actualToken);
            String nickname = userService.findNicknameById(userId);

            if (nickname == null || nickname.trim().isEmpty()) {
                return ResponseEntity.ok("");
            }

            return ResponseEntity.ok(nickname);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("닉네임 조회 실패: " + e.getMessage());
        }
    }

    //처음 닉네임,이미지 설정하기
    @PutMapping("/profile2")
    public ResponseEntity<String> updateUserProfile(
            @RequestHeader("Authorization") String token,
            @RequestBody ProfileUpdateDTO profileUpdateDTO) {
        // 디버깅: 클라이언트로부터 받은 데이터 확인
        System.out.println("받은 닉네임: " + profileUpdateDTO.getNickname());
        System.out.println("받은 프로필 URL: " + profileUpdateDTO.getProfileUrl());

        // 토큰에서 사용자 ID 추출
        Long userId = jwtUtil.getIdFromToken(token.replace("Bearer ", ""));

        // 서비스 호출
        userService.updateUserProfile(userId, profileUpdateDTO.getNickname(), profileUpdateDTO.getProfileUrl());
        return ResponseEntity.ok("프로필 업데이트 성공!");
    }

    //파일 업로드
    @PostMapping("/upload/profile-image")
    public ResponseEntity<Map<String, String>> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        try {
            System.out.println("파일 업로드 시작");
            System.out.println("파일 이름: " + file.getOriginalFilename());
            System.out.println("파일 타입: " + file.getContentType());
            System.out.println("파일 크기: " + file.getSize());

            String imageUrl = fileService.saveFile(file);
            System.out.println("이미지 URL: " + imageUrl);

            return ResponseEntity.ok(Map.of("url", imageUrl));
        } catch (Exception e) {
            e.printStackTrace(); // 서버 로그에 예외 출력
            return ResponseEntity.status(500).body(Map.of("message", "이미지 업로드에 실패했습니다."));
        }
    }

    // 비밀번호 변경
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> passwordData) {
        try {
            String currentPassword = passwordData.get("currentPassword");
            String newPassword = passwordData.get("newPassword");

            // 토큰에서 사용자 ID 추출
            Long userId = jwtUtil.getIdFromToken(token.replace("Bearer ", ""));

            // 비밀번호 변경 서비스 호출
            userService.changePassword(userId, currentPassword, newPassword);
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 회원 탈퇴
    @PutMapping("/deactivate")
    public ResponseEntity<String> deactivateUser(@RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtil.getIdFromToken(token.replace("Bearer ", ""));
            userService.deactivateUser(userId);
            return ResponseEntity.ok("회원 탈퇴가 성공적으로 처리되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/kakao-message")
    public ResponseEntity<String> sendKakaoMessage(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> request) {
        try {
            String actualToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getIdFromToken(actualToken);

            String message = request.get("message");
            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("메시지는 필수 입력 항목입니다.");
            }

            userService.sendMessage(userId, message);

            return ResponseEntity.ok("카카오톡 메시지가 성공적으로 전송되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("메시지 전송 실패: " + e.getMessage());
        }
    }

    @PostMapping("/kakao/authorize")
    public ResponseEntity<String> handleKakaoAuthorization(@RequestParam("code") String authorizationCode) {
        try {
            String newAccessToken = kakaoApiService.getNewAccessToken(authorizationCode);
            System.out.println("새로운 카카오 액세스 토큰: " + newAccessToken);

            // 토큰을 사용자 데이터베이스에 저장
            userService.updateKakaoAccessToken(newAccessToken);

            return ResponseEntity.ok("카카오톡 동의 완료 및 액세스 토큰 갱신 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("액세스 토큰 갱신 실패: " + e.getMessage());
        }
    }

}
