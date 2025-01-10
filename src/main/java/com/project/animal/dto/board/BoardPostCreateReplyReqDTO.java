package com.project.animal.dto.board;

import lombok.Data;

// 게시글 대댓글 작성 요청을 위한 DTO (댓글 작성 요청 DTO 상속)
@Data
public class BoardPostCreateReplyReqDTO extends BoardPostCreateCommentReqDTO {

    private String parentIdx;   // 부모 idx(자신의 부모 댓글 idx)

}
