package com.project.animal.service;

import org.springframework.stereotype.Service;
import com.project.animal.dto.user.RegisterDTO;
import com.project.animal.mapper.UserMapper;

@Service
public class UserService {
    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public void registerUser(RegisterDTO registerDTO) {
        userMapper.registerUser(registerDTO);
    }
}
