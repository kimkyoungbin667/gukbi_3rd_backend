package com.project.animal.service;

import com.project.animal.dto.chat.ChatRoomDTO;
import com.project.animal.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    public List<ChatRoomDTO> getChatRoomList(String userIdx) {
        return chatRepository.getChatRoomList(userIdx);
    }
}
