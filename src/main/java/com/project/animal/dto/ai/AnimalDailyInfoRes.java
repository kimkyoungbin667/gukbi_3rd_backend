package com.project.animal.dto.ai;

import lombok.Data;

// 반려동물 일일 식사량, 운동량, 몸무게 반환 DTO
@Data
public class AnimalDailyInfoRes {

    private String activityDate;    // 활동 일
    private String activityTime;    // 활동 시간
    private String mealAmount;      // 식사량
    private String exerciseDuration; // 운동 시간
    private String weight;          // 몸무게
    private String waterIntake;     // 물 섭취량
    private String note;            // 메모
}
