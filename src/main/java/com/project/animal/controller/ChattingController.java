package com.project.animal.controller;

import com.project.animal.dto.chat.SendMessageDTO;
import com.project.animal.dto.chat.TypingStatus;
import com.project.animal.service.ChatService;
import com.project.animal.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@Controller
@CrossOrigin(origins = "http://58.74.46.219:33333")
public class ChattingController {

    @Autowired
    private ChatService chatService;
    private JwtUtil jwtUtil;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

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

        message.setSenderProfile(chatService.getUserProfile(senderIdx));
        System.out.println("보낸 사람 : "+ senderIdx);

        // 메시지 저장
        int result = chatService.sendMessage(message);

        // 클라이언트로 반환할 메시지
        return message;
    }

    @MessageMapping("/room/{roomIdx}/typing")
    public void typing(@Payload TypingStatus typingStatus,
                       @DestinationVariable Long roomIdx,
                       SimpMessageHeaderAccessor headerAccessor) {

        String senderSessionId = headerAccessor.getSessionId();  // 보낸 사람의 세션 ID
        Long senderIdx = typingStatus.getSenderIdx();            // 보낸 사람 ID

        System.out.println("보낸 사람 세션 ID: " + senderSessionId);
        System.out.println("보낸 사람 ID: " + senderIdx);

        // ✅ 모든 사용자에게 전송
        messagingTemplate.convertAndSend(
                "/topic/room/" + roomIdx + "/typing",
                new TypingStatus(senderIdx, roomIdx, typingStatus.isTyping())
        );
    }


}