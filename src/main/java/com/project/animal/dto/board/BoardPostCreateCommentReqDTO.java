package com.project.animal.dto.board;

import lombok.Data;

// 게시글 댓글 작성 요청을 위한 DTO
@Data
public class BoardPostCreateCommentReqDTO {

    private Long boardIdx;      // 게시글 idx
    private Long authorIdx;        // 작성자 idx
    private String comment;     // 댓글 내용
    private String parentIdx;     // 부모 댓글 idx

}
