package com.project.animal.dto.board;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BoardCommentDTO {

    private Long commentIdx;       // 댓글 고유 Idx
    private String content;        // 댓글 내용
    private Long authorIdx;        // 작성자 Idx
    private Long authorToken;        // 작성자 토큰
    private Long parentIdx;        // 부모 댓글 Idx (null이면 댓글, 아니면 대댓글)
    private String authorNickname;   // 작성자 닉네임
    private String authorProfileUrl; // 작성자 프로필 이미지
}
