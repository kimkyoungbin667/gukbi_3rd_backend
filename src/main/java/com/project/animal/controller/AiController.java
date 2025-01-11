package com.project.animal.controller;

import com.project.animal.dto.ai.AiMessageReqDTO;
import com.project.animal.service.AiService;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://58.74.46.219:33333")
public class AiController {
    @Autowired
    private AiService aiService;

    @PostMapping("/chat")
    public ResponseEntity<String> getChatResponse(@RequestBody AiMessageReqDTO AiMessageReqDTO) {

        System.out.println(AiMessageReqDTO.getPrompt());
        String prompt = AiMessageReqDTO.getPrompt();
        String response = aiService.getChatResponse(prompt);
        return ResponseEntity.ok(response);  // React에 응답 반환
    }
}
