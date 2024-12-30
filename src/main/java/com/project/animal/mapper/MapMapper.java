package com.project.animal.mapper;

import com.project.animal.dto.map.PathReq;
import com.project.animal.dto.map.WalkReq;
import org.apache.ibatis.annotations.*;

@Mapper
public interface MapMapper {

    //산책 저장
    int addWalk(WalkReq walkReq);
    //산책 경로 저장 
    int addPath(PathReq pathReq);
}
