package com.project.animal.controller;

import com.project.animal.ResponseData.ResponseData;
import com.project.animal.dto.chat.ChatRoomDTO;
import com.project.animal.dto.chat.ChatRoomDetailDTO;
import com.project.animal.service.ChatService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {

    @Autowired
    private ChatService chatService;

    // 채팅방 목록 불러오기
    @GetMapping("/getChatRoomList")
    @ResponseBody
    public ResponseEntity<ResponseData> getChatRoomList(@RequestParam(value = "userIdx") Long userIdx) {
        ResponseData responseData = new ResponseData();

        System.out.println(userIdx);
        try{
            List<ChatRoomDTO> list = chatService.getChatRoomList(userIdx);

            if (list.isEmpty()) {
                responseData.setCode("204");
                responseData.setMsg("채팅방이 없습니다.");
                responseData.setData(list);
            }

            responseData.setData(list);
            return ResponseEntity.ok(responseData);

        } catch(Exception e){
            e.printStackTrace(); // 서버 로그에 오류 출력
            responseData.setCode("500"); // Internal Server Error
            responseData.setMsg("서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
            responseData.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseData);
        }


    }

    // 채팅방 내용 불러오기 불러오기
    @GetMapping("/getChatRoomMsg")
    @ResponseBody
    public ResponseEntity<ResponseData> getChatRoomDetail(@RequestParam(value = "roomIdx") Long roomIdx) {
        ResponseData responseData = new ResponseData();

        List<ChatRoomDetailDTO> list = chatService.getChatRoomDetail(roomIdx);

        System.out.println(list);
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