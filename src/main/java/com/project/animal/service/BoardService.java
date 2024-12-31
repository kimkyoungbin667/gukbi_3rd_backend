package com.project.animal.service;

import com.project.animal.dto.board.BoardDetailResponseDTO;
import com.project.animal.dto.board.BoardEditResponseDTO;
import com.project.animal.dto.board.BoardListResponseDTO;
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
    public List<BoardListResponseDTO> getBoardList(int limit, int offset) {return boardMapper.getBoardList(limit, offset);}

    // 게시글 상세보기
    public BoardDetailResponseDTO getBoardDetail(long boardIdx) {return boardMapper.getBoardDetail(boardIdx);}

    // 게시글 삭제하기
    public Integer deleteBoard(long boardIdx) {return boardMapper.deleteBoard(boardIdx);}

    // 조회수 올리기
    public Integer increaseView(long boardIdx) {return boardMapper.increaseView(boardIdx);}

    // 게시글 수정하기
    public Integer saveEditBoard(BoardEditResponseDTO boardEditResponseDTO) {
        return boardMapper.saveEditBoard(boardEditResponseDTO);}
}
