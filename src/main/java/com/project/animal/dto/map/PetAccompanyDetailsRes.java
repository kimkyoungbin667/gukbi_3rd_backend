package com.project.animal.dto.map;

import lombok.Data;

@Data
public class PetAccompanyDetailsRes {
    private int contentid;
    private String contenttypeid;
    private String title;
    private String tel;
    private String homepage;
    private String firstImage;
    private String firstImage2;
    private String addr1;
    private double mapx;
    private double mapy;
    private String overview;

    // Pet Accompany Info 필드
    private String relaAcdntRiskMtr;
    private String acmpyTypeCd;
    private String etcAcmpyInfo;
    private String acmpyPsblCpam;
    private String acmpyNeedMtr;
}
