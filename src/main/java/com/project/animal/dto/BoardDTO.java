package com.project.animal.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class BoardDTO {

    private String boardIdx;       //게시판 인덱스
    private String title;           //제목
    private String content;         //내용
    private String userId;          //작성자 아이디
    private Long userIdx;          //작성자 인덱스
    private String userName;        //작성자 이름
    private String created;         //생성일
    private String deleteYn;        //삭제여부
    private Integer good;           //좋아요

}
