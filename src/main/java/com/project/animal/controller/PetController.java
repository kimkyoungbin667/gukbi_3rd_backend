package com.project.animal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.animal.dto.pet.MedicalRecordDto;
import com.project.animal.dto.pet.PetDailyRecordDto;
import com.project.animal.dto.pet.PetDetailsRequestDto;
import com.project.animal.dto.pet.PetInfoRequestDto;
import com.project.animal.service.FileService;
import com.project.animal.service.PetService;
import com.project.animal.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pet")
@CrossOrigin(origins = "http://58.74.46.219:33333")
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
            // 인증 검증 및 사용자 확인
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("error", "Missing or invalid Authorization header"));
            }

            String actualToken = token.replace("Bearer ", "");
            if (!jwtUtil.validateToken(actualToken)) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired token"));
            }

            Long userId = jwtUtil.getIdFromToken(actualToken);

            // 파일 저장 및 URL 생성
            String relativeUrl = fileService.savePetFile(file);
            String absoluteUrl = "http://localhost:8080" + relativeUrl;

            // 데이터베이스 업데이트
            petService.updatePetImage(userId, petId, relativeUrl);

            // URL 반환
            return ResponseEntity.ok(Map.of("url", absoluteUrl));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Image upload failed", "message", e.getMessage()));
        }
    }


    @PostMapping("/details")
    public ResponseEntity<?> savePetDetails(
            @RequestHeader("Authorization") String token,
            @RequestBody PetDetailsRequestDto detailsRequest) {
        System.out.println("POST /api/pet/details 호출됨, 요청 데이터: " + detailsRequest);
        try {
            String actualToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getIdFromToken(actualToken);

            petService.savePetDetails(userId, detailsRequest);
            return ResponseEntity.ok(Map.of("message", "Pet details saved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to save pet details", "message", e.getMessage()));
        }
    }

    @GetMapping("/details/{petId}")
    public ResponseEntity<?> getPetDetails(
            @RequestHeader("Authorization") String token,
            @PathVariable Long petId) {
        System.out.println("GET /api/pet/details/" + petId + " 호출됨");
        try {
            String actualToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getIdFromToken(actualToken);

            var details = petService.getPetDetails(userId, petId);
            return ResponseEntity.ok(details);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch pet details", "message", e.getMessage()));
        }
    }

    @GetMapping("/medical-records/{petId}")
    public ResponseEntity<?> getMedicalRecords(
            @RequestHeader("Authorization") String token,
            @PathVariable Long petId) {
        try {
            String actualToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getIdFromToken(actualToken);

            List<Map<String, Object>> medicalRecords = petService.getMedicalRecords(userId, petId);
            return ResponseEntity.ok(medicalRecords);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch medical records", "message", e.getMessage()));
        }
    }

    @PostMapping("/medical-records")
    public ResponseEntity<?> addMedicalRecord(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> rawData) { // 원본 데이터 출력
        System.out.println("Raw Data Received: " + rawData);

        try {
            ObjectMapper mapper = new ObjectMapper(); // JSON 변환 확인
            MedicalRecordDto medicalRecord = mapper.convertValue(rawData, MedicalRecordDto.class);
            System.out.println("Converted Medical Record: " + medicalRecord);

            String actualToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getIdFromToken(actualToken);

            petService.addMedicalRecord(userId, medicalRecord);
            return ResponseEntity.ok(Map.of("message", "Medical record added successfully"));
        } catch (Exception e) {
            System.out.println("Failed to add medical record: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Failed to add medical record", "message", e.getMessage()));
        }
    }

    @DeleteMapping("/medical-records/{medicalId}")
    public ResponseEntity<?> deleteMedicalRecord(
            @RequestHeader("Authorization") String token,
            @PathVariable Long medicalId) {
        try {
            String actualToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getIdFromToken(actualToken);

            petService.deleteMedicalRecord(userId, medicalId);
            return ResponseEntity.ok(Map.of("message", "Medical record deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to delete medical record", "message", e.getMessage()));
        }
    }

    // 하루 기록 저장
    @PostMapping("/daily-records")
    public ResponseEntity<?> saveDailyRecord(
            @RequestHeader("Authorization") String token,
            @RequestBody PetDailyRecordDto dailyRecordDto) {
        System.out.println("Received Daily Record: " + dailyRecordDto);
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("error", "Missing or invalid Authorization header"));
            }

            String actualToken = token.replace("Bearer ", "");
            if (!jwtUtil.validateToken(actualToken)) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired token"));
            }

            petService.saveDailyRecord(dailyRecordDto);
            return ResponseEntity.ok(Map.of("message", "Daily record saved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to save daily record", "message", e.getMessage()));
        }
    }


    // 특정 펫의 하루 기록 조회
    @GetMapping("/daily-records/{petId}")
    public ResponseEntity<?> getDailyRecords(
            @PathVariable Long petId,
            @RequestParam(required = false) String section) {
        try {
            List<Map<String, Object>> records;

            // 섹션별 데이터 필터링
            switch (section) {
                case "meal":
                    records = petService.getMealRecords(petId); // 식사 관련 기록만 조회
                    break;
                case "exercise":
                    records = petService.getExerciseRecords(petId); // 운동 관련 기록만 조회
                    break;
                case "weight":
                    records = petService.getWeightRecords(petId); // 몸무게 관련 기록만 조회
                    break;
                default:
                    records = petService.getDailyRecords(petId); // 모든 기록 조회
                    break;
            }

            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch daily records", "message", e.getMessage()));
        }
    }

    // 하루 기록 삭제
    @DeleteMapping("/daily-records/{dailyId}")
    public ResponseEntity<?> deleteDailyRecord(
            @RequestHeader("Authorization") String token,
            @PathVariable Long dailyId) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("error", "Missing or invalid Authorization header"));
            }

            String actualToken = token.replace("Bearer ", "");
            if (!jwtUtil.validateToken(actualToken)) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired token"));
            }

            petService.deleteDailyRecord(dailyId);
            return ResponseEntity.ok(Map.of("message", "Daily record deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to delete daily record", "message", e.getMessage()));
        }
    }

    @GetMapping("/{petId}/graph-data")
    public ResponseEntity<?> getPetGraphData(
            @RequestHeader("Authorization") String token,
            @PathVariable Long petId,
            @RequestParam String startDate, // 시작 날짜
            @RequestParam String endDate    // 종료 날짜
    ) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("error", "Missing or invalid Authorization header"));
            }

            String actualToken = token.replace("Bearer ", "");
            if (!jwtUtil.validateToken(actualToken)) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired token"));
            }

            Long userId = jwtUtil.getIdFromToken(actualToken);
            Map<String, Object> graphData = petService.getPetGraphData(userId, petId, LocalDate.parse(startDate), LocalDate.parse(endDate));
            return ResponseEntity.ok(graphData);

        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch graph data", "message", e.getMessage()));
        }
    }

}
