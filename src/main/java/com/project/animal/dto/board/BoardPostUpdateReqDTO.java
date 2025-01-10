package com.project.animal.dto.board;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

// 게시글 수정 요청을 위한 DTO
@Data
public class BoardPostUpdateReqDTO {

    private Long boardIdx;      // 게시글 인덱스
    private Long authorIdx;     // 작성자 인덱스
    private String content;     // 수정 내용
    private List<String> existingImages;    // 업로드 이미지 경로 리스트

}
