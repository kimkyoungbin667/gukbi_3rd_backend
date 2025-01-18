package com.project.animal.controller;

import com.project.animal.dto.user.EmailVerificationDTO;
import com.project.animal.mapper.EmailVerificationMapper;
import com.project.animal.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "http://58.74.46.219:33333")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailVerificationMapper emailVerificationMapper;

    // 인증 코드 발송
    @PostMapping("/send-code")
    public ResponseEntity<?> sendVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        // 이메일 중복 확인
        if (emailVerificationMapper.isEmailExists(email)) {
            return ResponseEntity.status(409).body("이미 사용 중인 이메일입니다.");
        }

        String code = emailService.generateVerificationCode();

        EmailVerificationDTO verification = new EmailVerificationDTO();
        verification.setEmail(email);
        verification.setCode(code);
        verification.setExpirationTime(LocalDateTime.now().plusMinutes(10)); // 10분 유효
        emailVerificationMapper.insertVerificationCode(verification);

        try {
            emailService.sendVerificationEmail(email, code);
            return ResponseEntity.ok("인증 코드가 발송되었습니다.");
        } catch (MessagingException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("이메일 발송 실패");
        }
    }


    // 인증 코드 확인
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");

        EmailVerificationDTO verification = emailVerificationMapper.getVerificationCodeByEmail(email);
        if (verification == null || verification.getExpirationTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("인증 코드가 만료되었거나 존재하지 않습니다.");
        }

        if (!verification.getCode().equals(code)) {
            return ResponseEntity.badRequest().body("인증 코드가 올바르지 않습니다.");
        }

        return ResponseEntity.ok("이메일 인증 성공");
    }
}
