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

    List<PetAccompanyDetailsRes>getPetAccompanyDetails();

    int addCategoryFavorite(CategoryFavoriteAddReq categoryFavoriteAddReq);

    List<CategoryFavoriteGetRes> getFavoritesByUserIdx(Long userIdx);

    void deleteCategoryFavorite(Long userIdx, String id);

    int addAccompanyFavorite(AccompanyFavoriteAddReq accompanyFavoriteAddReq);

    List<AccompanyFavoriteGetRes> getAccompanyFavorite(AccompanyFavoriteGetReq accompanyFavoriteGetReq);

    void deleteAccompanyFavorite(AccompanyFavoriteAddReq accompanyFavoriteAddReq);


}
