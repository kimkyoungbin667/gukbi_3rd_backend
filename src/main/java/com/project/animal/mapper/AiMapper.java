package com.project.animal.mapper;

import com.project.animal.dto.ai.AnimalResDTO;
import com.project.animal.dto.chat.ChatRoomDTO;
import com.project.animal.dto.chat.ChatRoomDetailDTO;
import com.project.animal.dto.chat.SendMessageDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AiMapper {

    // 반려동물 목록 불러오기
    List<AnimalResDTO> getAnimalList(@Param("userIdx") long userIdx);
}
