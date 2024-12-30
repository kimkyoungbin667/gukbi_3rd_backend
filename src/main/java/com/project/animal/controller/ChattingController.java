package com.project.animal.controller;

import com.project.animal.dto.chat.SendMessageDTO;
import com.project.animal.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChattingController {

    @Autowired
    private ChatService chatService;

    @MessageMapping("/room/{roomIdx}/join")
    @SendTo("/topic/room/{roomIdx}")
    public SendMessageDTO joinRoom(SendMessageDTO message) {
        message.setMessage(message.getSenderIdx() + "님이 입장했습니다.");
        return message;
    }

    @MessageMapping("/room/{roomIdx}/send")
    @SendTo("/topic/room/{roomIdx}")
    public SendMessageDTO sendMessage(SendMessageDTO message) {

        System.out.println("받은 메세지 : "+ message);
        int result = chatService.sendMessage(message);
        return message;
    }
}