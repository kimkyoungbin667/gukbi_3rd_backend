package com.project.animal.controller;

import com.project.animal.ResponseData.ErrorMessage;
import com.project.animal.ResponseData.ResponseData;
import com.project.animal.dto.ai.AiMessageReqDTO;
import com.project.animal.dto.ai.AiSolutionReq;
import com.project.animal.dto.ai.AnimalResDTO;
import com.project.animal.service.AiService;
import com.project.animal.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://58.74.46.219:33333")
public class AiController {

    @Autowired
    private AiService aiService;

    private static final Logger logger = LoggerFactory.getLogger(AiController.class);

    @Autowired
    private JwtUtil jwtUtil;

    // ✅ 1. 고양이 AI 호출
    @PostMapping("/chat/cat")
    public ResponseEntity<String> getCatChatResponse(@RequestBody AiMessageReqDTO aiMessageReqDTO) {
        String prompt = aiMessageReqDTO.getPrompt();
        String response = aiService.getCatChatResponse(prompt);
        return ResponseEntity.ok(response);
    }

    // ✅ 2. 솔루션 AI 호출
    @PostMapping("/chat/solution")
    public ResponseEntity<String> getSolutionResponse(@RequestBody AiSolutionReq aiSolutionReq, @RequestHeader("Authorization") String token) {
        ResponseData responseData = new ResponseData();

        try {
            String actualToken = token.replace("Bearer ", "");

            if (!jwtUtil.validateToken(actualToken)) {
                System.err.println("권한 검증 실패");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("권한이 없습니다.");
            }

            String response = aiService.getSolutionResponse(aiSolutionReq);
            System.out.println(response);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error : ", e);
            responseData.setError(ErrorMessage.SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("솔루션 제공 중 오류가 발생했습니다.");
        }
    }

    // ✅ 3. 반려동물 목록 불러오기
    @GetMapping("/getAnimalList")
    public ResponseEntity<?> getAnimalList() {
        ResponseData responseData = new ResponseData();

        try {
            Long userIdx = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            List<AnimalResDTO> list = aiService.getAnimalList(userIdx);
            responseData.setData(list);

            return ResponseEntity.ok(responseData);

        } catch (ClassCastException e) {
            System.err.println("권한 검증 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);

        } catch (Exception e) {
            logger.error("Error : ", e);
            responseData.setError(ErrorMessage.SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseData);
        }
    }

    // ✅ 4. 특정 반려동물 상세 정보 조회
    @PostMapping("/getAnimalDetail")
    public ResponseEntity<Map<String, Object>> getAnimalDetail(@RequestBody AiSolutionReq aiSolutionReq, @RequestHeader("Authorization") String token) {
        ResponseData responseData = new ResponseData();
        Map<String, Object> map = new HashMap<>();

        try {
            String actualToken = token.replace("Bearer ", "");

            if (!jwtUtil.validateToken(actualToken)) {
                System.err.println("권한 검증 실패");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            map = aiService.getAnimalDetail(aiSolutionReq);
            return ResponseEntity.ok(map);

        } catch (Exception e) {
            logger.error("Error : ", e);
            responseData.setError(ErrorMessage.SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
