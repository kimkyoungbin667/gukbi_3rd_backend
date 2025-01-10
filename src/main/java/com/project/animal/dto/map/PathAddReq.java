package com.project.animal.dto.map;

import lombok.Data;

@Data
public class PathAddReq {
    private Long logId;
    private int sequence;
    private double latitude;
    private double longitude;
}