package com.project.animal.dto.pet;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PetInfoRequestDto {
    private String dogRegNo;
    private String rfidCd;
    private String ownerNm;
    private String ownerBirth;

    public PetInfoRequestDto(String dogRegNo, String rfidCd, String ownerNm, String ownerBirth) {
        this.dogRegNo = dogRegNo;
        this.rfidCd = rfidCd;
        this.ownerNm = ownerNm;
        this.ownerBirth = ownerBirth;
    }
}
