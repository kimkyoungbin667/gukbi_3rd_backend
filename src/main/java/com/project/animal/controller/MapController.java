package com.project.animal.controller;

import com.project.animal.ResponseData.ResponseData;
import com.project.animal.dto.map.PathAddReq;
import com.project.animal.dto.map.WalkAddReq;
import com.project.animal.dto.map.WalksGetReq;
import com.project.animal.dto.map.WalksGetRes;
import com.project.animal.service.MapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/map")
@CrossOrigin(origins = "http://58.74.46.219:33333")
public class MapController {

    @Autowired
    private MapService mapService;

    Logger logger = LoggerFactory.getLogger(MapController.class);

    @PostMapping("/walkRoutes/add")
    public ResponseEntity<ResponseData> addWalk(@RequestBody WalkAddReq walkReq) {
        ResponseData responseData = new ResponseData();

        Long logId = mapService.addWalk(walkReq);
        logger.info(logId + "");

        for (PathAddReq path : walkReq.getPaths()) {
            path.setLogId(logId);
            mapService.addPath(path);
        }

        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/test")
    public void test(@RequestBody WalkAddReq walkReq) {


    }

    @PostMapping("/walkRoutes/getWalks")
    public ResponseEntity<ResponseData> getWalkRoutes(@RequestBody WalksGetReq walksGetReq) {
        ResponseData responseData = new ResponseData();

        List<WalksGetRes> walksGetResList = mapService.getWalks(walksGetReq);

        logger.info(walksGetResList.toString());
        responseData.setData(walksGetResList);
        logger.info(responseData.toString());

        return ResponseEntity.ok(responseData);
    }
}
