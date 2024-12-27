package com.project.animal.mapper;

import com.project.animal.dto.chat.ChatRoomDTO;
import com.project.animal.dto.chat.ChatRoomDetailDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ChatMapper {
    
    // 채팅방 목록 불러오기
    List<ChatRoomDTO> getChatRoomList(@Param("userIdx") String userIdx);

    // 채팅방 상세보기
    List<ChatRoomDetailDTO> getChatRoomDetail(@Param("roomIdx") Long roomIdx);

    // 채팅 메세지 보내기
    Integer sendMessage();
}
