package com.project.animal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.animal.dto.user.RegisterDTO;
import com.project.animal.service.UserService;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:3000") // 3000 포트의 클라이언트 허용
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterDTO registerDTO) {
        if (registerDTO.getUserEmail() == null || registerDTO.getUserEmail().isEmpty()) {
            return ResponseEntity.badRequest().body("이메일은 필수 항목입니다.");
        }
        userService.registerUser(registerDTO);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

}
