package com.project.animal.mapper;

import com.project.animal.dto.map.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MapMapper {

    //산책 저장
    Long addWalk(WalkAddReq walkAddReq);
    //산책 경로 저장 
    Long addPath(PathAddReq pathAddReq);

    List<PathDTO> getPaths(Long userIdx);

    List<WalkDTO> getWalks(Long userIdx);
}
