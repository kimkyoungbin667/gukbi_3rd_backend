package com.project.animal.dto.pet;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SavePetInfoRequestDto {
    private String dogRegNo;
    private String rfidCd;
    private String dogName;
    private String sex;
    private String kindName;
    private String neuterStatus;
    private String organizationName;
    private String officeTel;
    private String approvalStatus;
    private String profileUrl;
}
