package com.project.animal.service;

import com.project.animal.dto.board.*;
import com.project.animal.mapper.BoardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardService {

    @Autowired
    private BoardMapper boardMapper;
    
    // 총 게시글 수 찾기
    public int getBoardListCount() { return boardMapper.getBoardListCount();}

    // 모든 게시글 불러오기 (페이지네이션 포함)
    public List<BoardPostListResDTO> getBoardList(int limit, int offset) {return boardMapper.getBoardList(limit, offset);}

    // 게시글 작성하기
    public Integer createBoardPost(BoardPostCreateReqDTO boardPostCreateReqDTO) {return boardMapper.createBoardPost(boardPostCreateReqDTO);}

    // 게시글 상세보기
    public BoardPostReadResDTO readBoardPost(BoardPostReadReqDTO boardPostReadReqDTO) {return boardMapper.readBoardPost(boardPostReadReqDTO);}

    // 게시글 수정하기
    public Integer updateBoardPost(BoardPostUpdateReqDTO boardPostUpdateReqDTO) {return boardMapper.updateBoardPost(boardPostUpdateReqDTO);}

    // 게시글 삭제하기
    public Integer deleteBoardPost(BoardPostDeleteReqDTO boardPostDeleteReqDTO) {return boardMapper.deleteBoardPost(boardPostDeleteReqDTO);}


    // 조회수 올리기
    public Integer increaseView(long boardIdx) {return boardMapper.increaseView(boardIdx);}



    // 댓글, 대댓글 조회하기
    public List<BoardPostReadCommentsResDTO> readBoardComments(Long longBoardIdx) {return boardMapper.readBoardComments(longBoardIdx);}

    // 댓글 작성하기
    public Integer createBoardComment(BoardPostCreateCommentReqDTO boardPostCreateCommentReqDTO) {return boardMapper.createBoardComment(boardPostCreateCommentReqDTO);}

    // 대댓글 작성하기
    public Integer createBoardReply(BoardPostCreateReplyReqDTO boardPostCreateReplyReqDTO) {return boardMapper.createBoardReply(boardPostCreateReplyReqDTO);}

    // 좋아요를 누른지 판단
    public Boolean isLikedPost(BoardPostUpLikeReqDTO boardPostUpLikeReqDTO) {return boardMapper.isLikedPost(boardPostUpLikeReqDTO);}

    // 좋아요 +1 하기
    public Integer upBoardPostLike(BoardPostUpLikeReqDTO boardPostUpLikeReqDTO) {return boardMapper.upBoardPostLike(boardPostUpLikeReqDTO);}
}
