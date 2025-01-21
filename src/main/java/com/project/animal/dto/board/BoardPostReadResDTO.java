package com.project.animal.dto.board;

import lombok.Data;

import java.util.List;

// 게시글 상세보기 응답에 대한 DTO
@Data
public class BoardPostReadResDTO {

    private Long boardIdx;              // 게시글 인덱스
    private String title;               // 제목
    private String content;             // 내용
    private Integer likeCount;          // 좋아요 수
    private Boolean isLiked;          // 좋아요 누른 유무
    private Long createdByUserIdx;      // 작성자 인덱스
    private String createdByUserNickname;// 작성자 닉네임
    private Integer viewCount;          // 조회수
    private String createdAt;        // 작성일
    private String imagePath;        // 쿼리에서 받은 이미지들의 경로
    private List<String> imageFiles;    // 업로드 이미지 리스트
    private Long logId;
    private String mapAccompanyId;
    private String mapCategoryId;

}

