package com.project.animal.controller;

import com.project.animal.ResponseData.ResponseData;
import com.project.animal.dto.map.PathReq;
import com.project.animal.dto.map.WalkReq;
import com.project.animal.service.MapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/map")
public class MapController {

    @Autowired
    private MapService mapService;

    Logger logger = LoggerFactory.getLogger(MapController.class);

    @PostMapping("/walkRoutes/add")
    public ResponseEntity<?> addWalk(@RequestBody WalkReq walkReq) {
        ResponseData responseData = new ResponseData();

        int logId = mapService.addWalk(walkReq);
        logger.info(logId + "");

        for (PathReq path : walkReq.getPaths()) {
            path.setLogId(logId);
            mapService.addPath(path);
        }

        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/test")
    public void test(@RequestBody WalkReq walkReq) {


    }
}
