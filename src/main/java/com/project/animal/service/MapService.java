package com.project.animal.service;

import com.project.animal.dto.map.PathReq;
import com.project.animal.dto.map.WalkReq;
import com.project.animal.mapper.MapMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MapService {

    @Autowired
    private MapMapper mapMapper;


    @Transactional
    public int addWalk(WalkReq walkReq) {
        mapMapper.addWalk(walkReq);
        return walkReq.getLogId();
    }

    @Transactional
    public int addPath(PathReq pathReq) {
        return mapMapper.addPath(pathReq);
    }

}
