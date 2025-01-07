package com.project.animal.dto.board;

import lombok.Data;

@Data
public class BoardReplyReqDTO extends BoardWriteCommentDTO {

    private String parentIdx;   // 부모 idx(자신의 부모 댓글 idx)

}
