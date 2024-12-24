package com.project.animal.controller;

import com.project.animal.ResponseData.ResponseData;
import com.project.animal.dto.chat.ChatRoomDTO;
import com.project.animal.dto.chat.ChatRoomDetailDTO;
import com.project.animal.service.ChatService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {

    private final ChatService chatService;

    // 채팅방 목록 불러오기
    @GetMapping("/getChatRoomList")
    @ResponseBody
    public ResponseEntity<ResponseData> getChatRoomList(@RequestParam(value = "userIdx") Long userIdx) {
        ResponseData responseData = new ResponseData();
        List<ChatRoomDTO> list = chatService.getChatRoomList(userIdx);

        if (list.isEmpty()) {
            responseData.setCode("500");
            responseData.setMsg("채팅방 목록 불러오기 실패");
            return ResponseEntity.ok(responseData);
        } else {
            responseData.setMsg("채팅방 목록 조회 성공");
            responseData.setData(list);
            return ResponseEntity.ok(responseData);
        }
    }

    // 채팅방 상세 불러오기
    @GetMapping("/getChatRoomDetail")
    @ResponseBody
    public ResponseEntity<ResponseData> getChatRoomDetail(@RequestParam(value = "roomIdx") Long roomIdx) {
        ResponseData responseData = new ResponseData();

        System.out.println(roomIdx);

        List<ChatRoomDetailDTO> list = chatService.getChatRoomDetail(roomIdx);

        if (list.isEmpty()) {
            responseData.setCode("500");
            responseData.setMsg("채팅방 상세 불러오기 실패");
            return ResponseEntity.ok(responseData);
        } else {
            responseData.setMsg("채팅방 상세 조회 성공");
            responseData.setData(list);
            return ResponseEntity.ok(responseData);
        }
    }
}