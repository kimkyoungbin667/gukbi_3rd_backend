package com.project.animal.ResponseData;


import lombok.Getter;

@Getter
public enum ErrorMessage {

    // -------------------------- Board -------------------------- //

    // 게시글 작성 관련
    BOARD_WRITE_FAILED("BOARD_400", "게시글 작성에 실패했습니다."),
    MISSING_REQUIRED_FIELDS("BOARD_400", "필수 입력값이 누락되었습니다."),

    // 수정 관련
    BOARD_UPDATE_FAILED("BOARD_400", "게시글 수정에 실패했습니다."),
    BOARD_NOT_FOUND("BOARD_404", "요청한 게시글을 찾을 수 없습니다."),
    NO_PERMISSION_TO_UPDATE("BOARD_403", "게시글을 수정할 권한이 없습니다."),

    // 삭제 관련
    BOARD_DELETE_FAILED("BOARD_400", "게시글 삭제에 실패했습니다."),
    NO_PERMISSION_TO_DELETE("BOARD_403", "게시글을 삭제할 권한이 없습니다."),

    // 조회 관련
    BOARD_FETCH_FAILED("BOARD_400", "게시글 조회에 실패했습니다."),
    NO_PERMISSION_TO_VIEW("BOARD_403", "이 게시글을 볼 권한이 없습니다."),

    BOARD_NO_DATA_FOUND("BOARD_200", "해당 데이터가 없습니다."),
    
    // 게시글 트래픽 관련
    TOO_MANY_REQUESTS("429", "요청이 너무 많습니다. 잠시 후 다시 시도해주세요."),

    // 페이지 번호
    INVALID_PAGE_VALUE("400", "유효하지 않은 페이지 번호입니다."),
    

    INTERNAL_SERVER_ERROR("BOARD_500", "서버 내부 오류가 발생했습니다."),

    // -------------------------- 서버 -------------------------- //

    //서버 에러
    SERVER_ERROR("500", "서버 내부 오류가 발생했습니다."),
    UNAUTHORIZED_ACCESS("500", "권한이 없습니다."),
    INVALID_REQUEST("500","유효하지 않은 요청입니다.");





    private final String code;
    private final String message;

    ErrorMessage(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
