package com.project.animal.service;

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
import java.util.Map;

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
            // API 요청 URL 생성
            String encodedServiceKey = URLEncoder.encode(originalServiceKey, StandardCharsets.UTF_8.toString());
            URI uri = UriComponentsBuilder.fromHttpUrl("https://apis.data.go.kr/1543061/animalInfoSrvc/animalInfo")
                    .queryParam("serviceKey", encodedServiceKey) // URL 인코딩된 서비스 키
                    .queryParam("dog_reg_no", requestDto.getDogRegNo())
                    .queryParam("rfid_cd", requestDto.getRfidCd() != null ? requestDto.getRfidCd() : "")
                    .queryParam("owner_nm", URLEncoder.encode(requestDto.getOwnerNm(), StandardCharsets.UTF_8.toString())) // 한글 인코딩
                    .queryParam("owner_birth", requestDto.getOwnerBirth() != null ? requestDto.getOwnerBirth() : "")
                    .queryParam("_type", "json")
                    .build(true) // true: 자동 인코딩 방지
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
        petMapper.insertPetInfo(petData);
    }
}
