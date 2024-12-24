package com.project.animal.dto.chat;

import lombok.Data;

// 채팅방 목록 불러오기
@Data
public class ChatRoomDTO {
    private Long roomIdx;
    private Long user1Idx;
    private String user1Name;
    private Long user2Idx;
    private String user2Name;
}
