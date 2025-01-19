package com.project.animal.dto.chat;

import lombok.Data;

import java.security.Timestamp;

// 채팅방 목록 불러오기
@Data
public class ChatRoomDTO {
    private Long roomIdx; // 채팅방 ID
    private Long opponentIdx; // 상대방 ID
    private String opponentName; // 상대방 이름
    private String opponentProfileUrl; // 상대방 프로필 URL
    private String opponentNickname; // 상대방 닉네임
}
