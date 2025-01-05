package com.project.animal.service;

import com.project.animal.dto.user.LoginDTO;
import com.project.animal.model.User;
import com.project.animal.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.project.animal.dto.user.RegisterDTO;
import com.project.animal.mapper.UserMapper;

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
        System.out.println("Retrieved User: " + user);
        if (user == null) {
            System.out.println("No user found for email: " + loginDTO.getEmail());
            throw new RuntimeException("Invalid email or password");
        }
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getUserPassword())) {
            System.out.println("Input Password: " + loginDTO.getPassword());
            System.out.println("Stored Password: " + user.getUserPassword());
            boolean isMatch = passwordEncoder.matches(loginDTO.getPassword(), user.getUserPassword());
            System.out.println("Password Match Result: " + isMatch);
        }

        return jwtUtil.generateToken(user.getUserEmail());
    }

    // 사용자 정보
    public User findUserProfileByEmail(String email) {
        return userMapper.findUserProfileByEmail(email);
    }
}
