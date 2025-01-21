package com.project.animal.controller;

import com.project.animal.ResponseData.ResponseData;
import com.project.animal.dto.map.*;
import com.project.animal.service.MapService;
import com.project.animal.service.PetService;
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
    @Autowired
    private PetService petService;

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

    @GetMapping("/getPetAccompanyDetail")
    public ResponseEntity<ResponseData> getPetAccompanyDetails() {
        ResponseData responseData = new ResponseData();
        List<PetAccompanyDetailsRes> petAccompanyDetailsResList = mapService.getPetAccompanyDetails();

        responseData.setData(petAccompanyDetailsResList);
        return ResponseEntity.ok(responseData);
    }

    @PostMapping("/category/addFavorite")
    public ResponseEntity<ResponseData> addCategoryFavorite(@RequestBody CategoryFavoriteAddReq categoryFavoriteAddReq) {
        ResponseData responseData = new ResponseData();
        mapService.addCategoryFavorite(categoryFavoriteAddReq);

        return ResponseEntity.ok(responseData);
    }

    @PostMapping("/category/getFavorite")
    public ResponseEntity<ResponseData> getCategoryFavorite(@RequestBody CategoryFavoriteGetReq categoryFavoriteGetReq) {
        ResponseData responseData = new ResponseData();
        List<CategoryFavoriteGetRes> result = mapService.getFavoritesByUserIdx(categoryFavoriteGetReq.getUserIdx());

        responseData.setData(result);
        return ResponseEntity.ok(responseData);
    }

    @PostMapping("/category/deleteFavorite")
    public ResponseEntity<ResponseData> deleteCategoryFavorite(@RequestBody CategoryFavoriteDeleteReq categoryFavoriteDeleteReq) {
    ResponseData responseData = new ResponseData();
        mapService.deleteCategoryFavorite(categoryFavoriteDeleteReq.getUserIdx(),categoryFavoriteDeleteReq.getId());

        return ResponseEntity.ok(responseData);
    }


    @PostMapping("/accompany/addFavorite")
    public ResponseEntity<ResponseData> addAccompanyFavorite(@RequestBody AccompanyFavoriteAddReq accompanyFavoriteAddReq) {
        ResponseData responseData = new ResponseData();
        mapService.addAccompanyFavorite(accompanyFavoriteAddReq);
        return ResponseEntity.ok(responseData);
    }

    @PostMapping("/accompany/getFavorite")
    public ResponseEntity<ResponseData> getAccompanyFavorite(@RequestBody AccompanyFavoriteGetReq accompanyFavoriteGetReq) {
        ResponseData responseData = new ResponseData();
        List<AccompanyFavoriteGetRes> result =mapService.getAccompanyFavoritesByUserIdx(accompanyFavoriteGetReq);
        responseData.setData(result);
        return ResponseEntity.ok(responseData);
    }

    @PostMapping("/accompany/deleteFavorite")
    public ResponseEntity<ResponseData> deleteAccompanyFavorite(@RequestBody AccompanyFavoriteAddReq accompanyFavoriteAddReq) {
        ResponseData responseData = new ResponseData();
        mapService.deleteAccompanyFavorite(accompanyFavoriteAddReq);
        return ResponseEntity.ok(responseData);
    }

    @PostMapping("/getPetInfo")
    public ResponseEntity<ResponseData> getPetInfo(@RequestBody WalksGetReq walksGetReq) {
        ResponseData responseData = new ResponseData();
        var result = petService.getPetsByUserId(walksGetReq.getUserIdx());
        logger.info(result.toString());
        responseData.setData(result);

        return ResponseEntity.ok(responseData);
    }

    @PostMapping("/accompany/getContentId")
    public ResponseEntity<ResponseData> getContentId(@RequestBody AccompanyContentIdGetReq accompanyContentIdGetReq) {
        ResponseData responseData = new ResponseData();
        PetAccompanyDetailsRes result =  mapService.getPetAccompanyDetails(accompanyContentIdGetReq);
        responseData.setData(result);
        return ResponseEntity.ok(responseData);
    }

    @PostMapping("/category/getContentId")
    public ResponseEntity<ResponseData> getId(@RequestBody AccompanyContentIdGetReq accompanyContentIdGetReq) {
        ResponseData responseData = new ResponseData();
        CategoryFavoriteGetRes result = mapService.getCategoryDetails(accompanyContentIdGetReq);
        responseData.setData(result);
        return ResponseEntity.ok(responseData);
    }


}
