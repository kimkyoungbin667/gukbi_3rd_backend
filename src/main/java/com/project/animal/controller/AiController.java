package com.project.animal.controller;

import com.project.animal.dto.ai.AiMessageReqDTO;
import com.project.animal.dto.ai.AnimalResDTO;
import com.project.animal.service.AiService;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
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

    @GetMapping("/getAnimalList")
    public ResponseEntity<List<AnimalResDTO>> getAnimalList() {

        List<AnimalResDTO> list = new ArrayList<>();

        AnimalResDTO animalResDTO = new AnimalResDTO();
        animalResDTO.setAge(4);
        animalResDTO.setBreed("웰시코기");
        animalResDTO.setName("춘식이");
        animalResDTO.setImageUrl("testDog.jpg");

        AnimalResDTO animalResDTO2 = new AnimalResDTO();
        animalResDTO2.setAge(2);
        animalResDTO2.setBreed("말티즈");
        animalResDTO2.setName("진돗개");
        animalResDTO2.setImageUrl("testDog2.jpg");

        AnimalResDTO animalResDTO3 = new AnimalResDTO();
        animalResDTO3.setAge(5);
        animalResDTO3.setBreed("먼치킨");
        animalResDTO3.setName("냥농냥");
        animalResDTO3.setImageUrl("testCat1.jpg");

        AnimalResDTO animalResDTO4 = new AnimalResDTO();
        animalResDTO4.setAge(10);
        animalResDTO4.setBreed("노르웨이 터키시 앙고라");
        animalResDTO4.setName("앙콜라");
        animalResDTO4.setImageUrl("testCat2.jpg");

        list.add(animalResDTO);
        list.add(animalResDTO2);
        list.add(animalResDTO3);
        list.add(animalResDTO4);

        return ResponseEntity.ok(list);
    }
}
