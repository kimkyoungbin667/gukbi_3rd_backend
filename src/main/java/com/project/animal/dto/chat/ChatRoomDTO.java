package com.project.animal.dto.chat;

import lombok.Data;

// 채팅방 목록 불러오기 DTO
@Data
public class ChatRoomDTO {
    private String roomIdx;
    private String user1Idx;
    private String user1Name;
    private String user2Idx;
    private String user2Name;
}
