package com.project.animal.controller;

import com.project.animal.ResponseData.ErrorMessage;
import com.project.animal.ResponseData.ResponseData;
import com.project.animal.dto.ai.AiMessageReqDTO;
import com.project.animal.dto.ai.AnimalResDTO;
import com.project.animal.service.AiService;
import com.project.animal.util.JwtUtil;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://58.74.46.219:33333")
public class AiController {

    @Autowired
    private AiService aiService;

    private static final Logger logger = LoggerFactory.getLogger(BoardController.class);

    @Autowired private JwtUtil jwtUtil;

    @PostMapping("/chat")
    public ResponseEntity<String> getChatResponse(@RequestBody AiMessageReqDTO AiMessageReqDTO) {

        System.out.println(AiMessageReqDTO.getPrompt());
        String prompt = AiMessageReqDTO.getPrompt();
        String response = aiService.getChatResponse(prompt);
        return ResponseEntity.ok(response);  // React에 응답 반환
    }

    // 게시글 파일 업로드하기
    @GetMapping("/getAnimalList")
    public ResponseEntity<?> getAnimalList() {
        ResponseData responseData = new ResponseData();

        try {
            Long userIdx = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            List<AnimalResDTO> list = new ArrayList<>();
            list = aiService.getAnimalList(userIdx);
            System.out.println(userIdx);
            System.out.println(list);

            responseData.setData(list);
            return ResponseEntity.ok(responseData);

        }
        // 토큰 검증 실패
        catch (ClassCastException e) {
            System.err.println("권한 검증 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
        }
        // 서버 에러 발생 시
        catch (Exception e) {
            logger.error("Error : ", e);
            responseData.setError(ErrorMessage.SERVER_ERROR);
            return ResponseEntity.ok(responseData);
        }
    }
}
