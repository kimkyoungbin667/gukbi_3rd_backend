package com.project.animal.dto.board;

import lombok.Data;

@Data
public class BoardListResponseDTO {

    private String boardIdx;        //게시글 인덱스
    private String title;           //제목
    private Integer good;           //추천수
    private String userName;        //작성자 이름
    private String created;         //생성일

}
