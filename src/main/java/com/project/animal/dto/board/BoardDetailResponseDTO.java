package com.project.animal.dto.board;

import lombok.Data;

@Data
public class BoardDetailResponseDTO {
    private Long boardIdx;              // 게시글 인덱스
    private String title;               // 제목
    private String content;             // 내용
    private Integer likeCount;          // 좋아요 수
    private Boolean isDeleted;          // 삭제 여부
    private Long createdByUserIdx;      // 작성자 인덱스
    private String createdByUserName;   // 작성자 이름
    private Long updatedByUserIdx;      // 변경한 사람 인덱스
    private Integer viewCount;          // 조회수
}
