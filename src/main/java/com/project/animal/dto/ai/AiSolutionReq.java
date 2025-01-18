package com.project.animal.dto.ai;

import lombok.Data;

@Data
public class AiSolutionReq {

    private String petId;       // 펫 ID
    private String startDate;   // 솔루션 시작일
    private String endDate;     // 솔루션 종료일
}
