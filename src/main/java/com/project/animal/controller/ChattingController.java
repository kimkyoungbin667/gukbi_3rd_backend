package com.project.animal.controller;

import com.project.animal.dto.chat.SendMessageDTO;
import com.project.animal.service.ChatService;
import com.project.animal.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChattingController {

    @Autowired
    private ChatService chatService;
    private JwtUtil jwtUtil;

    public ChattingController(JwtUtil jwtUtil, ChatService chatService) {
        this.jwtUtil = jwtUtil;
        this.chatService = chatService;
    }

    @MessageMapping("/room/{roomIdx}/join")
    @SendTo("/topic/room/{roomIdx}")
    public SendMessageDTO joinRoom(SendMessageDTO message) {
        message.setMessage("님이 입장했습니다.");
        return message;
    }

    @MessageMapping("/room/{roomIdx}/send")
    @SendTo("/topic/room/{roomIdx}")
    public SendMessageDTO sendMessage(@Payload SendMessageDTO message) {
        System.out.println("받은 메시지: " + message);

        // 토큰 검증
        if (!jwtUtil.validateToken(message.getSenderToken())) {
            throw new RuntimeException("Invalid sender token");
        }

        // 토큰에서 발신자 정보 추출
        Long senderIdx = jwtUtil.getIdFromToken(message.getSenderToken());
        message.setSenderIdx(senderIdx); // 발신자 ID 추가
        System.out.println("보낸 사람 : "+ senderIdx);

        // 메시지 저장
        int result = chatService.sendMessage(message);

        // 클라이언트로 반환할 메시지
        return message;
    }
}