package com.project.animal.dto.map;

import lombok.Data;

import java.util.List;

@Data
public class WalkDTO {
    private Long logId;
    private Long  userIdx;
    private String walkName;
    private String walkDate;
    private String startTime;
    private String endTime;
    private String distance;
    private String duration;
    private List<PathDTO> paths;

}
