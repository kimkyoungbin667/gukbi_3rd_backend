package com.project.animal.ResponseData;

import lombok.Data;

@Data
public class BoardResponseData extends ResponseData{
    private Integer totalPages; // 총 페이지 수 추가

    // 기본값이 성공
    public BoardResponseData() {
        super();
    }
}
