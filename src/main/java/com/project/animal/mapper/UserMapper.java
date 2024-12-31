package com.project.animal.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.project.animal.dto.user.RegisterDTO;

@Mapper
public interface UserMapper {
    void registerUser(RegisterDTO registerDTO);
}
