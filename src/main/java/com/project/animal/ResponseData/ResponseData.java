package com.project.animal.ResponseData;

import lombok.Data;

@Data
public class ResponseData {
    private String code;
    private String msg;
    private Object data;

    // 기본값이 성공
    public ResponseData() {
        this.code = "200";
        this.msg = "success";
    }
}
