package com.project.animal.repository;

import com.project.animal.dto.chat.ChatRoomDTO;
import com.project.animal.dto.chat.ChatRoomDetailDTO;
import com.project.animal.dto.chat.SendMessageDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatRepository {

    private final SqlSessionTemplate sql;

    public List<ChatRoomDTO> getChatRoomList(Long userIdx) {
        return sql.selectList("Chat.getChatRoomList", userIdx);
    }

    public List<ChatRoomDetailDTO> getChatRoomDetail(Long roomIdx) {
        return sql.selectList("Chat.getChatRoomDetail", roomIdx);
    }

    public int sendMessage(SendMessageDTO message) {
        return sql.insert("Chat.sendMessage", message);
    }
}
