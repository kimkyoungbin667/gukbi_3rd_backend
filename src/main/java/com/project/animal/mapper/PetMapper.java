package com.project.animal.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PetMapper {
    void insertPetInfo(Map<String, Object> petData);
    List<Map<String, Object>> findPetsByUserId(@Param("userIdx") Long userIdx);
    int deletePetById(@Param("userId") Long userId, @Param("petId") Long petId);
    void updatePetImage(@Param("userId") Long userId, @Param("petId") Long petId, @Param("profileUrl") String profileUrl);
}
