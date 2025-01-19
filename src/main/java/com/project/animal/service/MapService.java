package com.project.animal.service;

import com.project.animal.controller.MapController;
import com.project.animal.dto.map.*;
import com.project.animal.mapper.MapMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MapService {

    @Autowired
    private MapMapper mapMapper;

    Logger logger = LoggerFactory.getLogger(MapController.class);

    @Transactional
    public Long addWalk(WalkAddReq walkAddReq) {
        mapMapper.addWalk(walkAddReq);
        return walkAddReq.getLogId();
    }

    @Transactional
    public Long addPath(PathAddReq pathAddReq) {
        return mapMapper.addPath(pathAddReq);
    }


    @Transactional
    public List<WalksGetRes> getWalks(WalksGetReq walksGetReq) {
        List<WalkDTO> walks = mapMapper.getWalks(walksGetReq.getUserIdx());
        List<PathDTO> paths = mapMapper.getPaths(walksGetReq.getUserIdx());


        // 경로 데이터를 LogId 기준으로 그룹화
        Map<Long, List<PathDTO>> pathsMap = paths.stream()
                .collect(Collectors.groupingBy(PathDTO::getLogId));

        List<WalksGetRes> result = new ArrayList<>();

        for (WalkDTO walk : walks) {
            // 경로 필터링
            List<PathDTO> walkPaths = pathsMap.getOrDefault(walk.getLogId(), new ArrayList<>());

            // WalkDTO에서 WalksGetRes로 변환
            WalksGetRes walkReq = convertToWalksGetRes(walk, walkPaths);

            // 결과 리스트에 추가
            result.add(walkReq);
        }

        return result;
    }

    private WalksGetRes convertToWalksGetRes(WalkDTO walk, List<PathDTO> walkPaths) {
        WalksGetRes walkReq = new WalksGetRes();
        walkReq.setLogId(walk.getLogId());
        walkReq.setPetId(walk.getPetId());
        walkReq.setWalkName(walk.getWalkName());
        walkReq.setWalkDate(walk.getWalkDate());
        walkReq.setStartTime(walk.getStartTime());
        walkReq.setEndTime(walk.getEndTime());
        walkReq.setDistance(walk.getDistance());
        walkReq.setDuration(walk.getDuration());

        // 경로 리스트 변환
        List<PathsGetRes> pathList = walkPaths.stream()
                .map(path -> {
                    PathsGetRes pathRes = new PathsGetRes();
                    pathRes.setSequence(path.getSequence());
                    pathRes.setLatitude(path.getLatitude());
                    pathRes.setLongitude(path.getLongitude());
                    return pathRes;
                })
                .collect(Collectors.toList());

        walkReq.setPaths(pathList);
        return walkReq;
    }


    @Transactional
    public List<PetAccompanyDetailsRes> getPetAccompanyDetails() {
        return mapMapper.getPetAccompanyDetails();
    }


    public int addCategoryFavorite(CategoryFavoriteAddReq categoryFavoriteAddReq) {
        return mapMapper.addCategoryFavorite(categoryFavoriteAddReq);
    }

    public List<CategoryFavoriteGetRes> getFavoritesByUserIdx(Long userIdx) {
        return mapMapper.getFavoritesByUserIdx(userIdx);
    }

    public void deleteCategoryFavorite(Long userIdx, String id) {
        mapMapper.deleteCategoryFavorite(userIdx,id);
    }

    public int addAccompanyFavorite(AccompanyFavoriteAddReq accompanyFavoriteAddReq) {
        return mapMapper.addAccompanyFavorite(accompanyFavoriteAddReq);
    }
    public List<AccompanyFavoriteGetRes> getAccompanyFavoritesByUserIdx(AccompanyFavoriteGetReq accompanyFavoriteGetReq) {
        return mapMapper.getAccompanyFavorite(accompanyFavoriteGetReq);
    }

    public void deleteAccompanyFavorite(AccompanyFavoriteAddReq accompanyFavoriteAddReq) {
        mapMapper.deleteAccompanyFavorite(accompanyFavoriteAddReq);
    }

}
