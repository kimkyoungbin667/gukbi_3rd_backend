package com.project.animal.dto.board;

import lombok.Data;

// 게시글 댓글 조회 요청에 대한 DTO
@Data
public class BoardPostReadCommentsResDTO {

    private Long commentIdx;       // 댓글 고유 Idx
    private String content;        // 댓글 내용
    private Long authorIdx;        // 작성자 Idx
    private Long parentIdx;        // 부모 댓글 Idx (null이면 댓글, 아니면 대댓글)
    private String authorNickname;   // 작성자 닉네임
    private String authorProfileUrl; // 작성자 프로필 이미지
}
