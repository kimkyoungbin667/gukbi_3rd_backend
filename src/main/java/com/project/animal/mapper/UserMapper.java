package com.project.animal.mapper;

import com.project.animal.dto.user.LoginDTO;
import com.project.animal.model.User;
import org.apache.ibatis.annotations.Mapper;
import com.project.animal.dto.user.RegisterDTO;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    void registerUser(RegisterDTO registerDTO);
    User findByEmail(String userEmail);
    User findUserById(Long userId);
}
