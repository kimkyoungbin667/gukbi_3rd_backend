package com.project.animal.repository;

import com.project.animal.dto.chat.ChatRoomDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatRepository {

    private final SqlSessionTemplate sql;

    public List<ChatRoomDTO> getChatRoomList(String userIdx) {
        return sql.selectList("Chat.getChatRoomList", userIdx);
    }
}
