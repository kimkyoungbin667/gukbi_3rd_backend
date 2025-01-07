package com.project.animal.service;

import com.project.animal.dto.board.*;
import com.project.animal.mapper.BoardMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BoardService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BoardMapper boardMapper;
    
    // 총 게시글 수 찾기
    public int getBoardListCount() { return boardMapper.getBoardListCount();}

    // 모든 게시글 불러오기 (페이지네이션 포함)
    public List<BoardListResponseDTO> getBoardList(int limit, int offset) {return boardMapper.getBoardList(limit, offset);}

    // 게시글 상세보기
    public BoardDetailResponseDTO getBoardDetail(long boardIdx) {return boardMapper.getBoardDetail(boardIdx);}

    // 게시글 삭제하기
    public Integer deleteBoard(BoardIndexResponseDTO boardIndexResponseDTO) {return boardMapper.deleteBoard(boardIndexResponseDTO);}

    // 조회수 올리기
    public Integer increaseView(long boardIdx) {return boardMapper.increaseView(boardIdx);}

    // 게시글 수정하기
    public Integer saveEditBoard(BoardEditResponseDTO boardEditResponseDTO) {
        return boardMapper.saveEditBoard(boardEditResponseDTO);}

    // 게시글 작성하기
    public Integer writeBoard(BoardWriteResponseDTO boardWriteResponseDTO) {
        return boardMapper.writeBoard(boardWriteResponseDTO);
    }

    // 댓글, 대댓글 조회하기
    public List<BoardCommentDTO> getBoardComment(Long longBoardIdx) {return boardMapper.getBoardComment(longBoardIdx);}

    // 댓글 작성하기
    public Integer writeBoardComment(BoardWriteCommentDTO boardWriteCommentDTO) {
        return boardMapper.writeBoardComment(boardWriteCommentDTO);
    }

    // 대댓글 작성하기
    public Integer writeBoardReply(BoardReplyReqDTO boardReplyDTO) {
        return boardMapper.writeBoardReply(boardReplyDTO);
    }
}
