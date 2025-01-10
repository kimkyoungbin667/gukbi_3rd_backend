package com.project.animal.controller;

import com.project.animal.dto.pet.PetInfoRequestDto;
import com.project.animal.service.FileService;
import com.project.animal.service.PetService;
import com.project.animal.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/pet")
@CrossOrigin(origins = "http://localhost:3000")
public class PetController {

    private final JwtUtil jwtUtil;
    private final PetService petService;
    private final FileService fileService;

    public PetController(JwtUtil jwtUtil, PetService petService, FileService fileService) {
        this.jwtUtil = jwtUtil;
        this.petService = petService;
        this.fileService = fileService;
    }

    @GetMapping("/pet-info")
    public ResponseEntity<?> getPetInfo(
            @RequestHeader("Authorization") String token,
            @RequestParam String dogRegNo,
            @RequestParam(required = false) String rfidCd,
            @RequestParam String ownerNm,
            @RequestParam(required = false) String ownerBirth) {

        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("error", "Missing or invalid Authorization header"));
            }

            String actualToken = token.replace("Bearer ", "");
            if (!jwtUtil.validateToken(actualToken)) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired token"));
            }

            Long userId = jwtUtil.getIdFromToken(actualToken);

            PetInfoRequestDto requestDto = new PetInfoRequestDto(dogRegNo, rfidCd, ownerNm, ownerBirth);

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
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("error", "Missing or invalid Authorization header"));
            }

            String actualToken = token.replace("Bearer ", "");
            if (!jwtUtil.validateToken(actualToken)) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired token"));
            }

            Long userId = jwtUtil.getIdFromToken(actualToken);
            petService.savePetInfoToDb(userId, petData);

            return ResponseEntity.ok(Map.of("message", "Pet info saved successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to save pet info", "message", e.getMessage()));
        }
    }

    @GetMapping("/my-pets")
    public ResponseEntity<?> getMyPets(@RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("error", "Missing or invalid Authorization header"));
            }

            String actualToken = token.replace("Bearer ", "");
            if (!jwtUtil.validateToken(actualToken)) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired token"));
            }

            Long userId = jwtUtil.getIdFromToken(actualToken);
            var pets = petService.getPetsByUserId(userId);

            return ResponseEntity.ok(pets);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch pets", "message", e.getMessage()));
        }
    }

    @DeleteMapping("/{petId}")
    public ResponseEntity<?> deletePet(@RequestHeader("Authorization") String token, @PathVariable Long petId) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("error", "Missing or invalid Authorization header"));
            }

            String actualToken = token.replace("Bearer ", "");
            if (!jwtUtil.validateToken(actualToken)) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired token"));
            }

            Long userId = jwtUtil.getIdFromToken(actualToken);
            petService.deletePetById(userId, petId);

            return ResponseEntity.ok(Map.of("message", "Pet deleted successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to delete pet", "message", e.getMessage()));
        }
    }

    @PostMapping("/{petId}/upload-image")
    public ResponseEntity<Map<String, String>> uploadPetImage(
            @RequestHeader("Authorization") String token,
            @PathVariable Long petId,
            @RequestParam("file") MultipartFile file) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("error", "Missing or invalid Authorization header"));
            }

            String actualToken = token.replace("Bearer ", "");
            if (!jwtUtil.validateToken(actualToken)) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired token"));
            }

            Long userId = jwtUtil.getIdFromToken(actualToken);
            String imageUrl = fileService.savePetFile(file);

            petService.updatePetImage(userId, petId, imageUrl);

            return ResponseEntity.ok(Map.of("url", "http://localhost:8080" + imageUrl));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Image upload failed", "message", e.getMessage()));
        }
    }
}
