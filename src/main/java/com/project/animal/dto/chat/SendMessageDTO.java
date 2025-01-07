package com.project.animal.dto.chat;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.security.Timestamp;
import java.time.LocalDateTime;

@Data
public class SendMessageDTO {
    private Long roomIdx;          // 채팅방 idx
    private String senderToken;      // 보낸 사람 Token
    private Long senderIdx;            // 보낸 사람 idx
    private String message;       // 메시지 내용
    private LocalDateTime sentAt; // 클라이언트의 ISO 8601 문자열을 자동 변환
}
