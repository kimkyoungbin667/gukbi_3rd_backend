package com.project.animal.dto.board;

import lombok.Data;

// 게시글 상세보기 응답에 대한 DTO
@Data
public class BoardPostReadResDTO {
    private Long boardIdx;              // 게시글 인덱스
    private String title;               // 제목
    private String content;             // 내용
    private Integer likeCount;          // 좋아요 수
    private Long createdByUserIdx;      // 작성자 인덱스
    private String createdByUserNickname;// 작성자 닉네임
    private Integer viewCount;          // 조회수
    private String createdAt;        // 작성일
}

