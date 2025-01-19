package com.project.animal.controller;

import com.project.animal.ResponseData.ResponseData;
import com.project.animal.dto.chat.ChatRoomDTO;
import com.project.animal.dto.chat.ChatRoomDetailDTO;
import com.project.animal.service.ChatService;
import com.project.animal.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://58.74.46.219:33333")
public class ChatController {

    @Autowired
    private ChatService chatService;
    private JwtUtil jwtUtil;

    public ChatController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // 채팅방 목록 불러오기
    @GetMapping("/getChatRoomList")
    @ResponseBody
    public ResponseEntity<ResponseData> getChatRoomList(@RequestHeader("Authorization") String token) {
        ResponseData responseData = new ResponseData();

        System.out.println(token);
        // 토큰 값 추출
        token = token.replace("Bearer ", "");

        // 토큰 검증
        if (jwtUtil.validateToken(token)) {
            Long userIdx = jwtUtil.getIdFromToken(token);

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
            
        } else {
            throw new RuntimeException("유효하지 않은 토큰 값입니다!");
        }
    }

    // 채팅방 내용 불러오기 불러오기
    @GetMapping("/getChatRoomMsg")
    @ResponseBody
    public ResponseEntity<ResponseData> getChatRoomDetail(@RequestParam(value = "roomIdx") Long roomIdx) {
        ResponseData responseData = new ResponseData();

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

    // 현재 유저 닉네임 불러오기
    @GetMapping("/getUserNickname")
    @ResponseBody
    public ResponseEntity<ResponseData> getUserNickname(@RequestParam(value = "userIdx") String userIdx, @RequestHeader("Authorization") String token) {
        ResponseData responseData = new ResponseData();

        System.out.println(userIdx);
        token = token.replace("Bearer ", "");

        // 토큰 검증
        if (jwtUtil.validateToken(token)) {

            try {

                Long longUserIdx = jwtUtil.getIdFromToken(token);
                String nickname = chatService.getUserNickname(longUserIdx);

                if (nickname.isEmpty()) {
                    responseData.setCode("204");
                    responseData.setMsg("해당 유저의 닉네임이 없습니다.");
                    responseData.setData(null);
                }

                responseData.setData(nickname);
                return ResponseEntity.ok(responseData);

            } catch (Exception e) {
                e.printStackTrace(); // 서버 로그에 오류 출력
                responseData.setCode("500"); // Internal Server Error
                responseData.setMsg("서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
                responseData.setData(null);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseData);
            }

        } else {
            throw new RuntimeException("유효하지 않은 토큰 값입니다!");
        }


    }
}