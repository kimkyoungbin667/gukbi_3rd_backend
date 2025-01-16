package com.project.animal.dto.board;

import lombok.Data;

import java.util.List;

// 게시글 작성 요청에 대한 DTO
@Data
public class BoardPostCreateReqDTO {
    
    private Long boardIdx;          // 생성된 게시글 인덱스
    private String title;           // 작성한 게시글 제목
    private String content;         // 작성한 게시글 내용
    private Long authorIdx;         // 작성자 인덱스
    private List<String> imageFiles;    // 업로드 이미지 경로 리스트
    private Long logId;             // 

}
