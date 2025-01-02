package com.project.animal.dto.board;

import lombok.Data;

@Data
public class BoardWriteCommentDTO {

    private Long boardIdx;      // 게시글 idx
    private Long authorIdx;       // 작성자 idx;
    private String comment;     // 댓글 내용

}
