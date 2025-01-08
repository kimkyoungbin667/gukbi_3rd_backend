package com.project.animal.controller;

import com.project.animal.dto.pet.PetInfoRequestDto;
import com.project.animal.service.PetService;
import com.project.animal.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pet")
@CrossOrigin(origins = "http://localhost:3000")
public class PetController {

    private final JwtUtil jwtUtil;
    private final PetService petService;

    public PetController(JwtUtil jwtUtil, PetService petService) {
        this.jwtUtil = jwtUtil;
        this.petService = petService;
    }

    @GetMapping("/pet-info")
    public ResponseEntity<?> getPetInfo(
            @RequestHeader("Authorization") String token,
            @RequestParam String dogRegNo,
            @RequestParam(required = false) String rfidCd,
            @RequestParam String ownerNm,
            @RequestParam(required = false) String ownerBirth) {

        try {
            // JWT 유효성 검사
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("error", "Missing or invalid Authorization header"));
            }

            String actualToken = token.replace("Bearer ", "");
            if (!jwtUtil.validateToken(actualToken)) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired token"));
            }

            Long userId = jwtUtil.getIdFromToken(actualToken);
            System.out.println("유효한 사용자 ID: " + userId);

            // 요청 DTO 생성
            PetInfoRequestDto requestDto = new PetInfoRequestDto(dogRegNo, rfidCd, ownerNm, ownerBirth);

            // 서비스 호출
            String response = petService.getPetInfoFromApi(requestDto);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "API 요청 실패", "message", e.getMessage()));
        }
    }

    @PostMapping("/save-pet-info")
    public ResponseEntity<?> savePetInfo(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> petData) {

        try {
            // JWT 유효성 검사
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("error", "Missing or invalid Authorization header"));
            }

            String actualToken = token.replace("Bearer ", "");
            if (!jwtUtil.validateToken(actualToken)) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired token"));
            }

            Long userId = jwtUtil.getIdFromToken(actualToken);
            System.out.println("유효한 사용자 ID: " + userId);

            // 데이터 저장 호출
            petService.savePetInfoToDb(userId, petData);

            return ResponseEntity.ok(Map.of("message", "Pet info saved successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to save pet info", "message", e.getMessage()));
        }
    }
}
