package com.project.animal.controller;

import com.project.animal.ResponseData.ResponseData;
import com.project.animal.dto.chat.ChatRoomDTO;
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

    @GetMapping("/getChatRoomList")
    @ResponseBody
    public ResponseEntity<ResponseData> getChatRoomList(@RequestParam(value = "userIdx") String userIdx) {
        ResponseData responseData = new ResponseData();
        List<ChatRoomDTO> list = chatService.getChatRoomList(userIdx);

        System.out.println(userIdx);
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
}