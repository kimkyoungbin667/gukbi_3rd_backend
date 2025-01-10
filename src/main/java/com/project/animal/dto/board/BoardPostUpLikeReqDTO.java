package com.project.animal.dto.board;

import lombok.Data;

// 좋아요 기능 요청에 대한 DTO
@Data
public class BoardPostUpLikeReqDTO {

    private Long userIdx;       // 좋아요 누른 유저 인덱스 (클라에서 보내지 않음, jwt 검증되면 삽입시킴)
    private Long boardIdx;      // 좋아요한 게시글 인덱스

}
