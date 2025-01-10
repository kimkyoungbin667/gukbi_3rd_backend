package com.project.animal.dto.board;

import lombok.Data;

// 게시글 상세 요청에 대한 DTO
@Data
public class BoardPostReadReqDTO {

    private Long userIdx;               // 유저 인덱스 (요청으로 받지 않고, 토큰 검사 후 삽입됨)
    private Long boardIdx;              // 게시글 인덱스

}
