package com.project.animal.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface PetMapper {
    void insertPetInfo(Map<String, Object> petData);
    List<Map<String, Object>> findPetsByUserId(@Param("userIdx") Long userIdx);
    int deletePetById(@Param("userId") Long userId, @Param("petId") Long petId);
    void updatePetImage(@Param("userId") Long userId, @Param("petId") Long petId, @Param("profileUrl") String profileUrl);
    void insertOrUpdatePetDetails(Map<String, Object> detailsData);
    Map<String, Object> findPetDetailsByPetId(@Param("petId") Long petId);
    List<Map<String, Object>> findMedicalRecordsByPetId(@Param("petId") Long petId);

    void insertMedicalRecord(Map<String, Object> medicalRecordData);

    void deleteMedicalRecord(@Param("medicalId") Long medicalId);

    int isMedicalRecordOwnedByUser(@Param("userId") Long userId, @Param("medicalId") Long medicalId);

    // 하루 기록 삽입
    void insertDailyRecord(Map<String, Object> recordData);

    // 하루 기록 업데이트
    void updateDailyRecord(Map<String, Object> recordData);

    // 특정 펫의 하루 기록 조회
    List<Map<String, Object>> findDailyRecordsByPetId(@Param("petId") Long petId);

    @Select("SELECT daily_id, pet_id, activity_date, meal_amount, water_intake " +
            "FROM tb_pet_daily_activity " +
            "WHERE pet_id = #{petId} AND meal_amount IS NOT NULL")
    List<Map<String, Object>> findMealRecordsByPetId(@Param("petId") Long petId);

    @Select("SELECT daily_id, pet_id, activity_date, exercise_duration, exercise_distance " +
            "FROM tb_pet_daily_activity " +
            "WHERE pet_id = #{petId} AND exercise_duration IS NOT NULL")
    List<Map<String, Object>> findExerciseRecordsByPetId(@Param("petId") Long petId);

    @Select("SELECT daily_id, pet_id, activity_date, weight, notes " +
            "FROM tb_pet_daily_activity " +
            "WHERE pet_id = #{petId} AND weight IS NOT NULL")
    List<Map<String, Object>> findWeightRecordsByPetId(@Param("petId") Long petId);

    // 하루 기록 삭제
    void deleteDailyRecord(@Param("dailyId") Long dailyId);

    @Select("SELECT activity_date, " +
            "       DAYNAME(activity_date) AS dayOfWeek, " +
            "       COALESCE(meal_amount, 0) AS mealAmount, " +
            "       COALESCE(exercise_duration, 0) AS exerciseDuration, " +
            "       COALESCE(weight, 0) AS weight, " +
            "       COALESCE(water_intake, 0) AS waterIntake " +
            "FROM tb_pet_daily_activity " +
            "WHERE pet_id = #{petId}")
    List<Map<String, Object>> findGraphDataByPetId(@Param("petId") Long petId);


}
