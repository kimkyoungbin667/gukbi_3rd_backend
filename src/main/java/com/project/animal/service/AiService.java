package com.project.animal.service;

import com.project.animal.dto.ai.*;
import com.project.animal.mapper.AiMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Autowired
    AiMapper aiMapper;

    // ✅ 1. 고양이 AI 응답
    public String getCatChatResponse(String prompt) {
        String systemPrompt = "너는 귀엽고 장난기 많은 고양이 AI야. 대답할 때는 항상 '~냥' 말투를 사용해!";
        return callGptApi(systemPrompt, prompt, 300);
    }

    // ✅ 2. 솔루션 AI 응답
    public String getSolutionResponse(AiSolutionReq aiSolutionReq) {
        Long petId = Long.parseLong(aiSolutionReq.getPetId());

        // 🐾 반려동물 정보 조회
        AnimalResDTO animalInfo = aiMapper.getAnimalInfo(petId);
        List<AnimalPathRes> walkPath = aiMapper.getAnimalWalkPath(aiSolutionReq);
        List<AnimalDailyInfoRes> dailyInfo = aiMapper.getAnimalDailyInfo(aiSolutionReq);
        List<AnimalMedicalRes> medical = aiMapper.getAnimalMedical(aiSolutionReq);
        List<AnimalDetailInfoRes> detailInfo = aiMapper.getAnimalDetailInfo(aiSolutionReq);


        // 🐾 GPT 프롬프트 생성
        String prompt = buildSolutionPrompt(animalInfo, walkPath, dailyInfo, medical, detailInfo, aiSolutionReq.getStartDate(), aiSolutionReq.getEndDate());



        // 🐾 GPT 호출
        return callGptApi("너는 반려동물 전문가야. 반려동물 정보를 기반으로 건강, 운동, 식단 솔루션을 제공해줘.", prompt, 1500);
    }

    // ✅ 3. GPT API 호출 메서드
    private String callGptApi(String systemPrompt, String userPrompt, int maxTokens) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", new Object[]{
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userPrompt)
        });
        requestBody.put("max_tokens", maxTokens);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody != null && responseBody.containsKey("choices")) {
                Map<String, Object> choice = (Map<String, Object>) ((List<?>) responseBody.get("choices")).get(0);
                Map<String, Object> message = (Map<String, Object>) choice.get("message");
                return (String) message.get("content");
            } else {
                return "응답을 가져오지 못했습니다.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "GPT 호출 중 오류가 발생했습니다.";
        }
    }

    // ✅ 4. 솔루션 AI 프롬프트 구성
    private String buildSolutionPrompt(AnimalResDTO animalInfo, List<AnimalPathRes> walkPath,
                                       List<AnimalDailyInfoRes> dailyInfo, List<AnimalMedicalRes> medical,
                                       List<AnimalDetailInfoRes> detailInfo, String startDate, String endDate) {

        StringBuilder prompt = new StringBuilder();
        prompt.append("반려동물의 건강, 운동, 식단, 의료 기록을 분석하여 JSON 형식으로 전문적인 조언을 자세하게 제공해줘. 조언을 해줄 때 수치중심으로 구체적으로 조언해줘. 날짜를 말해줄땐 0월0일 형식으로 말해줘. 반려동물이 믹스견인 경우, 체형과 활동량에 따라 적절한 평균 운동량, 식사량, 체중을 추정하고 나이를 참고로 해서 제공해줘. \n" +
                "유사한 체형의 품종을 기반으로 평균 데이터를 계산하거나, 전체 견종 평균을 적용해줘.\n\n\n");

        prompt.append("해당 품종의 평균을 구할때 몇일 간의 평균을 구할때 일수는 해당 시작일은 "+startDate+"부터 "+endDate+"사이의 일수로 품종의 평균치를 구해줘 ");

        prompt.append("아래와 같은 JSON 형식으로 응답해줘:\n");
        prompt.append("{\n");
        prompt.append("  \"animalInfo\": {\n");
        prompt.append("    \"name\": \"반려동물 이름\",\n");
        prompt.append("    \"breed\": \"품종\",\n");
        prompt.append("    \"age\": \"나이\"\n");
        prompt.append("  },\n");
        prompt.append("  \"summaryData\": {\n");
        prompt.append("    \"totalExerciseDistance\": \"총 운동 거리 (km)\",\n");
        prompt.append("    \"totalExerciseTime\": \"총 운동 시간 (분)\",\n");
        prompt.append("    \"totalMealAmount\": \"총 식사량 (g)\",\n");
        prompt.append("    \"totalSnackAmount\": \"총 간식량 (g)\",\n");
        prompt.append("    \"totalWaterIntake\": \"총 물 섭취량 (ml)\"\n");
        prompt.append("  },\n");
        prompt.append("  \"averageData\": {\n");
        prompt.append("    \"averageExerciseDistance\": \"평균 산책 거리 (km)\",\n");
        prompt.append("    \"averageExerciseTime\": \"평균 산책 시간 (분)\",\n");
        prompt.append("    \"averageMealAmount\": \"평균 식사량 (g)\"\n");
        prompt.append("    \"averageWaterIntake\": \"평균 물 섭취량 (g)\"\n");
        prompt.append("  },\n");
        prompt.append("  \"medicalRecords\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"type\": \"의료 기록 종류\",\n");
        prompt.append("      \"description\": \"의료 기록 설명\",\n");
        prompt.append("      \"day\": \"날짜\",\n");
        prompt.append("    }\n");
        prompt.append("  ],\n");
        prompt.append("  \"solutions\": {\n");
        prompt.append("    \"overall\": \"전체적인 건강 상태 요약\",\n");
        prompt.append("    \"exercise\": \"운동량에 대한 조언\",\n");
        prompt.append("    \"diet\": \"식사량 및 영양 상태에 대한 조언\",\n");
        prompt.append("    \"medical\": \"의료 및 건강관리 조언\"\n");
        prompt.append("    \"waterIntake\": \"물 섭취에 대한 조언\"\n");
        prompt.append("  }\n");

        prompt.append("그리고 아래에 breedAverageData 는 해당 품종의 해당 나이에 대한 일반적인 평균에 대해 넣어주면 돼");
        prompt.append("  \"breedAverageData\": {\n");
        prompt.append("    \"averageExerciseDistance\": \"해당 품종 평균 산책 거리 (km)\",\n");
        prompt.append("    \"averageExerciseTime\": \"해당 품종 평균 산책 시간 (분)\",\n");
        prompt.append("    \"averageMealAmount\": \"해당 품종 평균 식사량 (g)\"\n");
        prompt.append("    \"averageWaterIntake\": \"해당 품종 평균 물 섭취량 (L)\"\n");
        prompt.append("  },\n");
        prompt.append("}\n\n");

        // 🐾 반려동물 나이 계산
        String birthDate = "정보 없음";
        int age = -1;

        if (detailInfo != null && !detailInfo.isEmpty()) {
            birthDate = detailInfo.get(0).getBirthDate();
            if (birthDate != null && !birthDate.isEmpty()) {
                age = calculateAge(birthDate);
                System.out.println(age);
            }
        }

        // 📌 반려동물 정보
        prompt.append("📌 **반려동물 정보**\n");
        prompt.append("- 이름: ").append(animalInfo.getDogName()).append("\n");
        prompt.append("- 품종: ").append(animalInfo.getKindName()).append("\n");
        prompt.append("- 나이: ").append(age != -1 ? age + "세" : "정보 없음").append("\n\n");

        // 📌 산책 정보
        prompt.append("📌 **산책 정보**\n");
        if (walkPath != null && !walkPath.isEmpty()) {
            for (AnimalPathRes path : walkPath) {
                prompt.append("- 날짜: ").append(path.getWalkDate() != null ? path.getWalkDate() : "정보 없음")
                        .append(", 거리: ").append(path.getDistance() != null ? path.getDistance() + "km" : "정보 없음")
                        .append(", 시간: ").append(path.getDuration() != null ? path.getDuration() + "분" : "정보 없음")
                        .append("\n");
            }
        } else {
            prompt.append("- 산책 정보가 없습니다.\n");
        }

        // 📌 일일 활동 정보
        prompt.append("\n📌 **일일 활동 정보**\n");
        if (dailyInfo != null && !dailyInfo.isEmpty()) {
            for (AnimalDailyInfoRes daily : dailyInfo) {
                prompt.append("- 날짜: ").append(daily.getActivityDate() != null ? daily.getActivityDate() : "정보 없음")
                        .append(", 식사량: ").append(daily.getMealAmount() != null ? daily.getMealAmount() + "g" : "정보 없음")
                        .append(", 운동시간: ").append(daily.getExerciseDuration() != null ? daily.getExerciseDuration() + "분" : "정보 없음")
                        .append(", 체중: ").append(daily.getWeight() != null ? daily.getWeight() + "kg" : "정보 없음")
                        .append(", 물 섭취량: ").append(daily.getWaterIntake() != null ? daily.getWaterIntake() + "L" : "정보 없음")
                        .append("\n");
            }
        } else {
            prompt.append("- 일일 활동 정보가 없습니다.\n");
        }

        // 📌 의료 기록
        prompt.append("\n📌 **의료 기록**\n");
        if (medical != null && !medical.isEmpty()) {
            for (AnimalMedicalRes record : medical) {
                prompt.append("- 종류: ").append(record.getRecordType() != null ? record.getRecordType() : "정보 없음")
                        .append(", 설명: ").append(record.getDescription() != null ? record.getDescription() : "정보 없음")
                        .append(", 진료일: ").append(record.getCreatedAt() != null ? record.getCreatedAt() : "정보 없음")
                        .append("\n");
            }
        } else {
            prompt.append("- 의료 기록이 없습니다.\n");
        }

        System.out.println(prompt.toString());
        return prompt.toString();
    }


    // ✅ 나이 계산 메서드
    private int calculateAge(String birthDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate birth = LocalDate.parse(birthDate, formatter);
            LocalDate today = LocalDate.now();
            return Period.between(birth, today).getYears();
        } catch (Exception e) {
            System.err.println("나이 계산 중 오류 발생: " + e.getMessage());
            return -1;  // 오류 발생 시 -1 반환
        }
    }


    public List<AnimalResDTO> getAnimalList(Long userIdx) {
        return aiMapper.getAnimalList(userIdx);
    }

    public Map<String, Object> getAnimalDetail(AiSolutionReq aiSolutionReq) {
        Map<String, Object> map = new HashMap<>();

        Long longPetId = Long.parseLong(aiSolutionReq.getPetId());
        map.put("animalInfo", aiMapper.getAnimalInfo(longPetId));
        map.put("walkPath", aiMapper.getAnimalWalkPath(aiSolutionReq));
        map.put("dailyInfo", aiMapper.getAnimalDailyInfo(aiSolutionReq));
        map.put("medical", aiMapper.getAnimalMedical(aiSolutionReq));
        map.put("detailInfo", aiMapper.getAnimalDetailInfo(aiSolutionReq));

        return map;
    }
}
