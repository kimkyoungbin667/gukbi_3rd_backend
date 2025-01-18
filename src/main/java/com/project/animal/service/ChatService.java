package com.project.animal.service;

import com.project.animal.dto.chat.*;
import com.project.animal.mapper.ChatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class ChatService {

    @Autowired
    private ChatMapper chatMapper;

    // 특정 채팅방 채팅내역 불러오기
    public List<ChatRoomDetailDTO> getChatRoomDetail(Long roomIdx) {
        return chatMapper.getChatRoomDetail(roomIdx);
    }

    // 자신이 참여하는 모든 채팅방 목록 불러오기
    public List<ChatRoomDTO> getChatRoomList(Long userIdx) {

        System.out.println(userIdx);
        return chatMapper.getChatRoomList(userIdx);
    }

    // 메세지 전송하기
    public int sendMessage(SendMessageDTO message) {return chatMapper.sendMessage(message);}

    // 유저 프로필 가져오기
    public String getUserProfile(Long senderIdx) {
        return chatMapper.getUserProfile(senderIdx);
    }

    // 채팅 전송된 이미지 저장하기
    public Integer saveSendImage(SaveImageDTO saveImageDTO) {
        saveImageDTO.setImageUrl("/upload/"+saveImageDTO.getImageUrl());
        return chatMapper.saveSendImage(saveImageDTO);
    }
}
