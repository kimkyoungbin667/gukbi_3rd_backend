package com.project.animal.dto.board;

import lombok.Data;

// 게시글 수정 요청을 위한 DTO
@Data
public class BoardPostUpdateReqDTO {

    private Long boardIdx;      // 게시글 인덱스
    private Long authorIdx;     // 작성자 인덱스
    private String content;     // 수정 내용

}
