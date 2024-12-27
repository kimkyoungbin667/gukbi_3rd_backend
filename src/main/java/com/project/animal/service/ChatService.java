package com.project.animal.service;

import com.project.animal.dto.chat.ChatRoomDTO;
import com.project.animal.dto.chat.ChatRoomDetailDTO;
import com.project.animal.dto.chat.SendMessageDTO;
import com.project.animal.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    public List<ChatRoomDetailDTO> getChatRoomDetail(Long roomIdx) {
        return chatRepository.getChatRoomDetail(roomIdx);
    }

    public List<ChatRoomDTO> getChatRoomList(Long userIdx) {
        return chatRepository.getChatRoomList(userIdx);
    }

    public int sendMessage(SendMessageDTO message) {
        return chatRepository.sendMessage(message);
    }
}
