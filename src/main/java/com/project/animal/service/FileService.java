package com.project.animal.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {

    private final String uploadDir = System.getProperty("user.dir") + "/src/main/upload/";

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

            return "upload/" + uniqueFileName; // URL 반환
        } catch (IOException e) {
            System.err.println("파일 저장 중 오류: " + e.getMessage());
            throw new RuntimeException("파일 저장 중 오류 발생: " + e.getMessage());
        }
    }

    public String savePetFile(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath); // 디렉토리가 없으면 생성
            }

            String uniqueFileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(uniqueFileName);

            file.transferTo(filePath.toFile()); // 파일 저장

            // 서버에서 접근 가능한 URL 반환
            return "/upload/" + uniqueFileName;
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류 발생: " + e.getMessage());
        }
    }

    public List<String> saveFiles(List<MultipartFile> files) {
        try {
            System.out.println("다중 파일 저장 시작");
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("디렉토리 생성 완료: " + uploadPath.toAbsolutePath());
            }

            List<String> savedFilePaths = new ArrayList<>();

            for (MultipartFile file : files) {
                String uniqueFileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
                Path filePath = uploadPath.resolve(uniqueFileName);
                file.transferTo(filePath.toFile());
                savedFilePaths.add("upload/" + uniqueFileName); // URL 경로 저장
                System.out.println("파일 저장 성공: " + filePath.toAbsolutePath());
            }

            return savedFilePaths;
        } catch (IOException e) {
            System.err.println("다중 파일 저장 중 오류: " + e.getMessage());
            throw new RuntimeException("다중 파일 저장 중 오류 발생: " + e.getMessage());
        }
    }

    public void deleteFiles(List<String> filePaths) {
        try {
            for (String filePath : filePaths) {
                Path fullPath = Paths.get(uploadDir, filePath.replace("upload/", ""));
                Files.deleteIfExists(fullPath);
                System.out.println("파일 삭제 성공: " + fullPath.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("파일 삭제 중 오류: " + e.getMessage());
            throw new RuntimeException("파일 삭제 중 오류 발생: " + e.getMessage());
        }
    }


    public List<String> uploadFiles(List<MultipartFile> newImages) {
        List<String> uploadedFilePaths = new ArrayList<>(); // 저장된 파일 경로 리스트

        for (MultipartFile file : newImages) {
            try {
                // 파일 저장 경로 설정
                String uniqueFileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
                Path uploadPath = Paths.get(System.getProperty("user.dir") + "/src/main/upload/"); // 상대 경로 -> 절대 경로
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath); // 디렉토리 생성
                }
                Path filePath = uploadPath.resolve(uniqueFileName);

                // 파일 저장
                file.transferTo(filePath.toFile());

                // 저장된 경로 리스트에 추가
                uploadedFilePaths.add("upload/" + uniqueFileName);

            } catch (IOException e) {
                System.err.println("파일 업로드 중 오류 발생: " + e.getMessage());
                throw new RuntimeException("파일 업로드 실패: " + e.getMessage());
            }
        }

        return uploadedFilePaths; // 저장된 파일 경로 반환
    }

}
