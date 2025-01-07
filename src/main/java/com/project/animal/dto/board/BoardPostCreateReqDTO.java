package com.project.animal.dto.board;

import lombok.Data;

// 게시글 작성 요청을 처리하기 위한 DTO
@Data
public class BoardPostCreateReqDTO {
    
    private String title;           // 작성한 게시글 제목
    private String content;         // 작성한 게시글 내용
    private Long authorIdx;         // 작성자 인덱스

}
