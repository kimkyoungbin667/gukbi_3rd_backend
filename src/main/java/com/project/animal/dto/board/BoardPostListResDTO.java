package com.project.animal.dto.board;

import lombok.Data;

// 게시글 전체 조회 응답을 위한 DTO
@Data
public class BoardPostListResDTO {

    private Long boardIdx;              // 게시글 인덱스
    private String title;               // 제목
    private Integer likeCount;          // 좋아요 수
    private String createdByUserNickname;// 작성자 닉네임
    private Integer viewCount;          // 조회수
    private String createdAt;        // 작성일
            
}
