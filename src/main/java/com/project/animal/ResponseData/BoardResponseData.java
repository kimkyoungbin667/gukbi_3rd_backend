package com.project.animal.ResponseData;

import lombok.Data;

@Data
public class BoardResponseData {
    private String code;
    private String msg;
    private Object data;
    private Integer totalPages; // 총 페이지 수 추가

    // 기본값이 성공
    public BoardResponseData() {
        this.code = "200";
        this.msg = "success";
    }
}
