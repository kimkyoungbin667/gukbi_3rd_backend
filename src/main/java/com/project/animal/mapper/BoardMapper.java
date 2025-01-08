package com.project.animal.mapper;

import com.project.animal.dto.board.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BoardMapper {

    // 게시글 목록 불러오기
    List<BoardPostListResDTO> getBoardList(@Param("limit") int limit, @Param("offset") int offset);

    // 게시글 수 불러오기
    Integer getBoardListCount();

    // 게시글 작성하기
    Integer createBoardPost(BoardPostCreateReqDTO boardPostCreateReqDTO);

    // 게시글 상세보기
    BoardPostReadResDTO readBoardPost(BoardPostReadReqDTO boardPostReadReqDTO);

    // 게시글 수정하기
    Integer updateBoardPost(BoardPostUpdateReqDTO boardPostUpdateReqDTO);

    // 게시글 삭제하기
    Integer deleteBoardPost(BoardPostDeleteReqDTO boardPostDeleteReqDTO);



    // 조회수 올리기
    Integer increaseView(@Param("boardIdx") long boardIdx);

    // 댓글, 대댓글 불러오기
    List<BoardPostReadCommentsResDTO> readBoardComments(Long longBoardIdx);

    // 댓글 작성하기
    Integer createBoardComment(BoardPostCreateCommentReqDTO boardPostCreateCommentDTO);

    // 대댓글 작성하기
    Integer createBoardReply(BoardPostCreateReplyReqDTO boardReplyDTO);

    // 좋아요를 누른지 판단
    Boolean isLikedPost(BoardPostUpLikeReqDTO boardPostUpLikeReqDTO);

    // 좋아요 +1 하기
    Integer upBoardPostLike(BoardPostUpLikeReqDTO boardPostUpLikeReqDTO);
}
