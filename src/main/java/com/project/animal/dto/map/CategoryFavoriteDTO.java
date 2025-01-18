package com.project.animal.dto.map;

import lombok.Data;

@Data
public class CategoryFavoriteDTO {
    private Long favoriteIdx;       // 즐겨찾기 고유 ID
    private Integer userIdx;        // 사용자 고유 ID
    private String id;              // 장소 고유 ID
    private String placeName;       // 장소 이름
    private String phone;           // 전화번호
    private String placeUrl;        // 장소 상세 URL
    private String addressName;     // 지번 주소
    private String roadAddressName; // 도로명 주소
    private Double x;               // X 좌표 (경도)
    private Double y;               // Y 좌표 (위도)
}