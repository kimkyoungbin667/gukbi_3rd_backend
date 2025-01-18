package com.project.animal.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TypingStatus {
    private Long senderIdx;   // 입력 중인 사람의 ID
    private Long roomIdx;     // 채팅방 ID
    private boolean typing;   // 입력 중 상태(true/false)
}
