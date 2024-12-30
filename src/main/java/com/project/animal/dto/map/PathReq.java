package com.project.animal.dto.map;

import lombok.Data;

@Data
public class PathReq {
    int logId;
    int sequence;
    double latitude;
    double longitude;
}