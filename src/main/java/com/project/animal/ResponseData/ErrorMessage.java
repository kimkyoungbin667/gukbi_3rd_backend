package com.project.animal.ResponseData;


import lombok.Getter;

@Getter
public enum ErrorMessage {


    //게시판
    WRITE_FALSE("200", "글자 입력 실패"),
    BOARD_SIZE_ZERO("200", "조회된 데이터가 없습니다."),
    BOARD_UPDTE_FALSE("200", "글자 수정 실패"),
    
    
    //전체적
    ALL_ERROR("500", "서버 내부 오류가 발생했습니다.");

    



    private final String code;
    private final String message;

    ErrorMessage(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
