package com.project.animal.mapper;

import com.project.animal.dto.ai.*;
import com.project.animal.dto.board.PathResDTO;
import com.project.animal.dto.chat.ChatRoomDTO;
import com.project.animal.dto.chat.ChatRoomDetailDTO;
import com.project.animal.dto.chat.SendMessageDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AiMapper {

    // 반려동물 목록 불러오기
    List<AnimalResDTO> getAnimalList(@Param("userIdx") long userIdx);


    // 반려동물 일일 식사량, 운동량, 몸무게 가져오기 
    List<AnimalDailyInfoRes> getAnimalDailyInfo(AiSolutionReq aiSolutionReq);

    // 반려동물 의료기록 가져오기
    List<AnimalMedicalRes> getAnimalMedical(AiSolutionReq aiSolutionReq);

    // 반려동물 산책 정보 가져오기
    List<AnimalPathRes> getAnimalWalkPath(AiSolutionReq aiSolutionReq);

    // 반려동물 상세정보 가져오기
    List<AnimalDetailInfoRes> getAnimalDetailInfo(AiSolutionReq aiSolutionReq);

    // 해당 동물 정보 가져오기
    AnimalResDTO getAnimalInfo(@Param("longPetId") Long longPetId);
}
