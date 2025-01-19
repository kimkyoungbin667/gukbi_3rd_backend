package com.project.animal.dto.board;

import lombok.Data;

// 장소 불러오기
@Data
public class GetLocationOrPathReqDTO {
    
    private Long userIdx;       // 유저 인덱스
    private String kind;        // 불러올 종류 (즐겨찾기 장소[location] or 산책경로[path])
}
