package com.project.animal.dto.board;

import lombok.Data;

// 게시글의 댓글(대댓글) 삭제 요청에 대한 DTO
@Data
public class BoardPostDeleteCommentReqDTO {

    private long commentIdx;        // 삭제할 댓글(대댓글) 인덱스
}
