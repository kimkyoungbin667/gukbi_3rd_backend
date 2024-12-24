package com.project.animal.mapper;

import com.project.animal.dto.chat.ChatRoomDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ChatMapper {
    List<ChatRoomDTO> getChatRoomList(@Param("userIdx") String userIdx);
}
