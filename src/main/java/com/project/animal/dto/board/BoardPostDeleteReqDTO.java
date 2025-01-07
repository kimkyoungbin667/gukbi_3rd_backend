package com.project.animal.dto.board;

import lombok.Data;

// 게시글 삭제 요청에 대한 DTO
@Data
public class BoardPostDeleteReqDTO {
    private long boardIdx;      // 삭제할 게시글 인덱스
}
