package com.project.animal.dto.pet;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MedicalRecordDto {
    @JsonProperty("pet_id") // JSON 키 "pet_id"와 매핑
    private Long petId;
    @JsonProperty("record_type") // JSON 키 "record_type"와 매핑
    private String recordType;
    @JsonProperty("record_date")
    private String recordDate;
    private String description;
    @JsonProperty("next_due_date")
    private String nextDueDate;
    @JsonProperty("clinic_name")
    private String clinicName;
    @JsonProperty("vet_name")
    private String vetName;
    private String notes;
}
