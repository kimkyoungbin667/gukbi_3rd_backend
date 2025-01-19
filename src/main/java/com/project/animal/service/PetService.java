package com.project.animal.service;

import com.project.animal.dto.pet.MedicalRecordDto;
import com.project.animal.dto.pet.PetDailyRecordDto;
import com.project.animal.dto.pet.PetDetailsRequestDto;
import com.project.animal.dto.pet.PetInfoRequestDto;
import com.project.animal.mapper.PetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

@Service
public class PetService {

    private static final Logger logger = LoggerFactory.getLogger(PetService.class);
    private final String originalServiceKey = "uhTTbiUzM0qBmdZsJhdabGkv9z7EyZNxxpRT0PycpmDpM+NLAo4YoillPVZAa7BFDBut7OLKEIEaDWPgJvjz5A==";

    private final PetMapper petMapper;

    public PetService(PetMapper petMapper) {
        this.petMapper = petMapper;
    }

    public String getPetInfoFromApi(PetInfoRequestDto requestDto) throws Exception {
        try {
            String encodedServiceKey = URLEncoder.encode(originalServiceKey, StandardCharsets.UTF_8.toString());
            URI uri = UriComponentsBuilder.fromHttpUrl("https://apis.data.go.kr/1543061/animalInfoSrvc/animalInfo")
                    .queryParam("serviceKey", encodedServiceKey)
                    .queryParam("dog_reg_no", requestDto.getDogRegNo())
                    .queryParam("rfid_cd", requestDto.getRfidCd() != null ? requestDto.getRfidCd() : "")
                    .queryParam("owner_nm", URLEncoder.encode(requestDto.getOwnerNm(), StandardCharsets.UTF_8.toString()))
                    .queryParam("owner_birth", requestDto.getOwnerBirth() != null ? requestDto.getOwnerBirth() : "")
                    .queryParam("_type", "json")
                    .build(true)
                    .toUri();

            logger.info("API 요청 URL: {}", uri);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json");
            headers.set("User-Agent", "Java-Client");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                logger.error("API 응답 오류: 상태 코드 {}", response.getStatusCode());
                throw new Exception("API 응답 오류");
            }

            logger.info("API 응답 성공");
            return response.getBody();
        } catch (Exception e) {
            logger.error("API 요청 중 오류 발생: {}", e.getMessage());
            throw e;
        }
    }

    public void savePetInfoToDb(Long userId, Map<String, Object> petData) {
        petData.put("userIdx", userId); // 사용자 ID 추가
        petMapper.insertPetInfo(petData);
    }

    public List<Map<String, Object>> getPetsByUserId(Long userId) {
        return petMapper.findPetsByUserId(userId);
    }

    public void deletePetById(Long userId, Long petId) {
        int rowsAffected = petMapper.deletePetById(userId, petId);
        if (rowsAffected == 0) {
            throw new RuntimeException("No pet found for the given user and pet ID.");
        }
    }

    public void updatePetImage(Long userId, Long petId, String imageUrl) {
        try {
            petMapper.updatePetImage(userId, petId, imageUrl);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update pet image", e);
        }
    }

    public void savePetDetails(Long userId, PetDetailsRequestDto detailsRequest) {
        if (!isPetOwnedByUser(userId, detailsRequest.getPetId())) {
            throw new RuntimeException("Unauthorized access to pet details.");
        }

        Map<String, Object> detailsData = Map.of(
                "petId", detailsRequest.getPetId(),
                "birthDate", detailsRequest.getBirthDate(),
                "healthStatus", detailsRequest.getHealthStatus(),
                "dietaryRequirements", detailsRequest.getDietaryRequirements(),
                "allergies", detailsRequest.getAllergies(),
                "notes", detailsRequest.getNotes()
        );

        petMapper.insertOrUpdatePetDetails(detailsData);
    }

    public Map<String, Object> getPetDetails(Long userId, Long petId) {
        if (!isPetOwnedByUser(userId, petId)) {
            throw new RuntimeException("Unauthorized access to pet details.");
        }
        return petMapper.findPetDetailsByPetId(petId);
    }

    public boolean isPetOwnedByUser(Long userId, Long petId) {
        var pets = petMapper.findPetsByUserId(userId);
        return pets.stream().anyMatch(pet -> pet.get("pet_id").equals(petId));
    }

    public List<Map<String, Object>> getMedicalRecords(Long userId, Long petId) {
        if (!isPetOwnedByUser(userId, petId)) {
            throw new RuntimeException("Unauthorized access to medical records.");
        }
        return petMapper.findMedicalRecordsByPetId(petId);
    }

    public void addMedicalRecord(Long userId, MedicalRecordDto medicalRecord) {
        if (!isPetOwnedByUser(userId, medicalRecord.getPetId())) {
            throw new RuntimeException("Unauthorized access to pet medical records.");
        }

        System.out.println("Adding Medical Record for Pet ID: " + medicalRecord.getPetId()); // 확인 출력

        Map<String, Object> recordData = Map.of(
                "petId", medicalRecord.getPetId(),
                "recordType", medicalRecord.getRecordType(),
                "recordDate", medicalRecord.getRecordDate(),
                "description", medicalRecord.getDescription(),
                "nextDueDate", medicalRecord.getNextDueDate(),
                "clinicName", medicalRecord.getClinicName(),
                "vetName", medicalRecord.getVetName(),
                "notes", medicalRecord.getNotes()
        );

        petMapper.insertMedicalRecord(recordData);
    }

    public void deleteMedicalRecord(Long userId, Long medicalId) {
        if (!isMedicalRecordOwnedByUser(userId, medicalId)) {
            throw new RuntimeException("Unauthorized access to delete medical record.");
        }
        petMapper.deleteMedicalRecord(medicalId);
    }

    private boolean isMedicalRecordOwnedByUser(Long userId, Long medicalId) {
        return petMapper.isMedicalRecordOwnedByUser(userId, medicalId) > 0;
    }

    // 하루 기록 저장
    public void saveDailyRecord(PetDailyRecordDto dailyRecordDto) {
        Map<String, Object> recordData = new HashMap<>();
        recordData.put("dailyId", dailyRecordDto.getDailyId());
        recordData.put("petId", dailyRecordDto.getPetId());
        recordData.put("activityDate", dailyRecordDto.getActivityDate());
        recordData.put("activityTime", dailyRecordDto.getActivityTime());

        if (dailyRecordDto.getMealAmount() != null) {
            recordData.put("mealAmount", dailyRecordDto.getMealAmount());
        }
        if (dailyRecordDto.getExerciseDuration() != null) {
            recordData.put("exerciseDuration", dailyRecordDto.getExerciseDuration());
        }
        if (dailyRecordDto.getExerciseDistance() != null) {
            recordData.put("exerciseDistance", dailyRecordDto.getExerciseDistance());
        }
        if (dailyRecordDto.getWeight() != null) {
            recordData.put("weight", dailyRecordDto.getWeight());
        }
        if (dailyRecordDto.getWaterIntake() != null) {
            recordData.put("waterIntake", dailyRecordDto.getWaterIntake());
        }
        if (dailyRecordDto.getNotes() != null) {
            recordData.put("notes", dailyRecordDto.getNotes());
        }

        if (dailyRecordDto.getDailyId() == null) {
            petMapper.insertDailyRecord(recordData);
        } else {
            petMapper.updateDailyRecord(recordData);
        }
    }

    // 특정 펫의 하루 기록 조회
    public List<Map<String, Object>> getDailyRecords(Long petId) {
        return petMapper.findDailyRecordsByPetId(petId); // 모든 기록 조회
    }

    public List<Map<String, Object>> getMealRecords(Long petId) {
        System.out.println("Fetched records: " + petMapper.findDailyRecordsByPetId(petId));
        return petMapper.findMealRecordsByPetId(petId); // 식사 기록만 조회
    }

    public List<Map<String, Object>> getExerciseRecords(Long petId) {
        return petMapper.findExerciseRecordsByPetId(petId); // 운동 기록만 조회
    }

    public List<Map<String, Object>> getWeightRecords(Long petId) {
        return petMapper.findWeightRecordsByPetId(petId); // 몸무게 기록만 조회
    }

    // 하루 기록 삭제
    public void deleteDailyRecord(Long dailyId) {
        petMapper.deleteDailyRecord(dailyId);
    }

    public Map<String, Object> getPetGraphData(Long userId, Long petId, LocalDate startDate, LocalDate endDate) {
        // 사용자와 펫 관계 확인
        if (!isPetOwnedByUser(userId, petId)) {
            throw new RuntimeException("Unauthorized access to pet data.");
        }

        // DB에서 그래프 데이터 조회
        List<Map<String, Object>> records = petMapper.findGraphDataByPetId(
                petId,
                startDate.toString(),
                endDate.toString()
        );

        // 데이터 그룹화 초기화
        Map<String, List<Double>> groupedData = new HashMap<>();
        groupedData.put("mealAmount", new ArrayList<>(Collections.nCopies(7, 0.0)));
        groupedData.put("exerciseDuration", new ArrayList<>(Collections.nCopies(7, 0.0)));
        groupedData.put("weight", new ArrayList<>(Collections.nCopies(7, 0.0)));
        groupedData.put("waterIntake", new ArrayList<>(Collections.nCopies(7, 0.0)));

        // 요일 인덱스 매핑
        Map<String, Integer> dayIndex = Map.of(
                "Monday", 0, "Tuesday", 1, "Wednesday", 2,
                "Thursday", 3, "Friday", 4, "Saturday", 5, "Sunday", 6
        );

        // 데이터 처리
        for (Map<String, Object> record : records) {
            try {
                String dayOfWeek = (String) record.get("dayOfWeek");
                if (!dayIndex.containsKey(dayOfWeek)) continue; // 유효하지 않은 요일 스킵

                int index = dayIndex.get(dayOfWeek);

                // 각 필드 데이터 업데이트
                groupedData.get("mealAmount").set(
                        index,
                        groupedData.get("mealAmount").get(index) + safeConvertToDouble(record.get("mealAmount"))
                );
                groupedData.get("exerciseDuration").set(
                        index,
                        groupedData.get("exerciseDuration").get(index) + safeConvertToDouble(record.get("exerciseDuration"))
                );
                groupedData.get("weight").set(
                        index,
                        safeConvertToDouble(record.get("weight"))
                ); // 몸무게는 누적하지 않음
                groupedData.get("waterIntake").set(
                        index,
                        groupedData.get("waterIntake").get(index) + safeConvertToDouble(record.get("waterIntake"))
                );
            } catch (Exception e) {
                System.err.println("Error processing record: " + record);
                e.printStackTrace();
            }
        }

        // 그래프 데이터 생성
        return Map.of(
                "labels", List.of("월", "화", "수", "목", "금", "토", "일"),
                "datasets", List.of(
                        Map.of(
                                "label", "식사량 (g)",
                                "data", groupedData.get("mealAmount"),
                                "backgroundColor", "rgba(75, 192, 192, 0.6)",
                                "borderColor", "rgba(75, 192, 192, 1)"
                        ),
                        Map.of(
                                "label", "운동 시간 (분)",
                                "data", groupedData.get("exerciseDuration"),
                                "backgroundColor", "rgba(153, 102, 255, 0.6)",
                                "borderColor", "rgba(153, 102, 255, 1)"
                        ),
                        Map.of(
                                "label", "몸무게 (kg)",
                                "data", groupedData.get("weight"),
                                "type", "line",
                                "backgroundColor", "rgba(255, 99, 132, 0.6)",
                                "borderColor", "rgba(255, 99, 132, 1)"
                        ),
                        Map.of(
                                "label", "물 섭취량 (ml)",
                                "data", groupedData.get("waterIntake"),
                                "backgroundColor", "rgba(54, 162, 235, 0.6)",
                                "borderColor", "rgba(54, 162, 235, 1)"
                        )
                )
        );
    }


    // 안전하게 숫자 변환하는 메서드
    private double safeConvertToDouble(Object value) {
        if (value == null) return 0.0;
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

}
