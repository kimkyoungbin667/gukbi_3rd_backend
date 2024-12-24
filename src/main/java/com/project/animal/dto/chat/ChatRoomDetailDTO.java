package com.project.animal.dto.chat;

import lombok.Data;

// 채팅방 상세 페이지
@Data
public class ChatRoomDetailDTO {
    private Long roomIdx;
    private Long messageIdx;
    private Long senderIdx;
    private String message;
}
