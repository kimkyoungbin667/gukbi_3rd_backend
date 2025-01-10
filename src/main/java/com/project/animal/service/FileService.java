package com.project.animal.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {

    private final String uploadDir = "C:/Users/202016017/Desktop/gukbi/3rd_Project/gukbi_3rd_backend/uploads/"; // 절대 경로 사용
    private final String petUploadDir = "C:/Users/202016017/Desktop/gukbi/3rd_Project/gukbi_3rd_backend/uploads/pets/";


    public String saveFile(MultipartFile file) {
        try {
            System.out.println("파일 이름: " + file.getOriginalFilename());
            System.out.println("파일 타입: " + file.getContentType());
            System.out.println("파일 크기: " + file.getSize());

            // 파일 저장 경로 설정
            Path uploadPath = Paths.get(uploadDir); // 절대 경로 사용
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath); // 디렉토리가 없으면 생성
                System.out.println("디렉토리 생성 완료: " + uploadPath.toAbsolutePath());
            }

            String uniqueFileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(uniqueFileName);
            System.out.println("저장 경로: " + filePath.toAbsolutePath());

            file.transferTo(filePath.toFile()); // 파일 저장
            System.out.println("파일 저장 성공");

            return "/uploads/" + uniqueFileName; // URL 반환
        } catch (IOException e) {
            System.err.println("파일 저장 중 오류: " + e.getMessage());
            throw new RuntimeException("파일 저장 중 오류 발생: " + e.getMessage());
        }
    }

    public String savePetFile(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(petUploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String uniqueFileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(uniqueFileName);

            file.transferTo(filePath.toFile());

            // URL에 http://localhost:8080 추가
            return "http://localhost:8080/uploads/pets/" + uniqueFileName;
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류 발생: " + e.getMessage());
        }
    }




}
