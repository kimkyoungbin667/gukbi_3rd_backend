package com.project.animal.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface PetMapper {
    void insertPetInfo(Map<String, Object> petData);
}
