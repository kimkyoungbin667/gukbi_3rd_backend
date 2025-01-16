package com.project.animal.dto.pet;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PetDailyRecordDto {
    @JsonProperty("daily_id")
    private Long dailyId;  // JSON의 "daily_id"와 매핑

    @JsonProperty("pet_id")
    private Long petId;  // JSON의 "pet_id"와 매핑

    @JsonProperty("activity_date")
    private String activityDate;  // JSON의 "activity_date"와 매핑

    @JsonProperty("activity_time")
    private String activityTime;  // JSON의 "activity_time"와 매핑

    @JsonProperty("meal_amount")
    private Double mealAmount;  // JSON의 "meal_amount"와 매핑

    @JsonProperty("exercise_duration")
    private Integer exerciseDuration;  // JSON의 "exercise_duration"와 매핑

    @JsonProperty("exercise_distance")
    private Double exerciseDistance;  // JSON의 "exercise_distance"와 매핑

    @JsonProperty("weight")
    private Double weight;  // JSON의 "weight"와 매핑

    @JsonProperty("water_intake")
    private Double waterIntake;  // JSON의 "water_intake"와 매핑

    @JsonProperty("notes")
    private String notes;  // JSON의 "notes"와 매핑
}
