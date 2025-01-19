package com.project.animal.mapper;

import com.project.animal.dto.chat.ChatRoomDTO;
import com.project.animal.dto.chat.ChatRoomDetailDTO;
import com.project.animal.dto.chat.SaveImageDTO;
import com.project.animal.dto.chat.SendMessageDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ChatMapper {
    
    // 채팅방 목록 불러오기
    List<ChatRoomDTO> getChatRoomList(@Param("userIdx") Long userIdx);

    // 채팅방 상세보기
    List<ChatRoomDetailDTO> getChatRoomDetail(@Param("roomIdx") Long roomIdx);

    // 채팅 메세지 보내기
    Integer sendMessage(SendMessageDTO message);

    // 유저 프로필 가져오기
    String getUserProfile(@Param("senderIdx") Long senderIdx);

    // 채팅으로 전송한 이미지 저장하기
    Integer saveSendImage(SaveImageDTO saveImageDTO);
}
